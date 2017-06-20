package me.shadaj.simple.react.core

import me.shadaj.simple.react.core.fascade.{ComponentInstance, PrivateComponentClass, React}

import scala.scalajs.js
import scala.scalajs.js.ConstructorTag
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

trait WithRaw {
  def raw: js.Dynamic = {
    this.asInstanceOf[js.Dynamic].__raw_ref
  }
}

abstract class Component {
  type Props

  type State

  type Def <: Definition

  @inline private def valueObject[T](value: T): js.Object = {
    js.Dynamic.literal(
      "__wrapped" -> true,
      "value" -> value.asInstanceOf[js.Any]
    )
  }

  @inline private def extractValue[T](obj: js.Object): T = {
    val ret = obj.asInstanceOf[js.Dynamic].value.asInstanceOf[T]

    if (ret.isInstanceOf[WithRaw]) {
      ret.asInstanceOf[js.Dynamic].__raw_ref = obj
    }

    ret
  }

  @ScalaJSDefined
  abstract class Definition(jsProps: Any) extends React.Component(jsProps) {
    def initialState: State

    this.asInstanceOf[PrivateComponentClass].stateR = valueObject(initialState)

    @JSName("props_scala")
    @inline final def props: Props = {
      extractValue[Props](this.asInstanceOf[PrivateComponentClass].propsR)
    }

    @JSName("state_scala")
    @inline final def state: State = {
      extractValue[State](this.asInstanceOf[PrivateComponentClass].stateR)
    }

    @JSName("setState_scala")
    @inline final def setState(s: State): Unit = {
      this.asInstanceOf[PrivateComponentClass].setStateR(valueObject(s))
    }

    def componentWillMount(): Unit = {}

    def componentDidMount(): Unit = {}

    def componentWillReceiveProps(props: Props): Unit = {}

    {
      val orig = this.asInstanceOf[js.Dynamic].componentWillReceiveProps.asInstanceOf[js.Function1[Props, Unit]]
      this.asInstanceOf[js.Dynamic].componentWillReceiveProps = (props: js.Dynamic) => {
        orig.call(
          this,
          props.value
        )
      }
    }

    def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = true

    {
      val orig = this.asInstanceOf[js.Dynamic].shouldComponentUpdate.asInstanceOf[js.Function2[Props, State, Boolean]]
      this.asInstanceOf[js.Dynamic].shouldComponentUpdate = (nextProps: js.Object, nextState: js.Object) => {
        orig.call(
          this,
          extractValue[Props](nextProps).asInstanceOf[js.Any],
          extractValue[State](nextState).asInstanceOf[js.Any]
        )
      }
    }

    def componentWillUpdate(nextProps: Props, nextState: State): Unit = {}

    {
      val orig = this.asInstanceOf[js.Dynamic].componentWillUpdate.asInstanceOf[js.Function2[Props, State, Unit]]
      this.asInstanceOf[js.Dynamic].componentWillUpdate = (nextProps: js.Object, nextState: js.Object) => {
        orig.call(
          this,
          extractValue[Props](nextProps).asInstanceOf[js.Any],
          extractValue[State](nextState).asInstanceOf[js.Any]
        )
      }
    }

    def componentDidUpdate(prevProps: Props, prevState: State): Unit = {}

    {
      val orig = this.asInstanceOf[js.Dynamic].componentDidUpdate.asInstanceOf[js.Function2[Props, State, Unit]]
      this.asInstanceOf[js.Dynamic].componentDidUpdate = (prevProps: js.Object, prevState: js.Object) => {
        orig.call(
          this,
          extractValue[Props](prevProps).asInstanceOf[js.Any],
          extractValue[State](prevState).asInstanceOf[js.Any]
        )
      }
    }

    def componentWillUnmount(): Unit = {}

    @JSName("render")
    def render(): ComponentInstance
  }

  def apply(p: Props)(implicit constructorTag: ConstructorTag[Def]): ComponentInstance = {
    val component = constructorTag.constructor
    component.displayName = getClass.getSimpleName

    val propsObj = valueObject(p)

    React.createElement(component, propsObj)
  }
}