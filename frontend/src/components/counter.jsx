import React, { Component } from "react";

class Counter extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: 0
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }
  handleSubmit(event) {
    alert(this.state.value);
    event.preventDefault();
  }
  handleChange(event) {
    this.setState({ value: event.target.value });
  }
  // Now it's made a controlled (stateless) component to implement 'Single Source of Truth' pattern

  /* a classic approach for 'binding' event handlers */
  // constructor() {
  //   super(); // Syntax error: 'this' is not allowed before super()
  //   this.handleIncrement = this.handleIncrement.bind(this);
  // }

  // 'componentDidUpdate' hook of the UPDATEing phase
  componentDidUpdate(prevProps, prevState) {
    console.log("Previous Props", prevProps);
    console.log("Previous States", prevState);
    if (prevProps.counter.value === this.props.counter.value) {
      console.log("didn't updated!!!");
    }
  }

  componentWillUnmount() {
    console.log("Component - Unmount");
  }

  render() {
    console.log("Counter - Rendered");
    return (
      <div className="buttons">
        <h4>Oferta numer: {this.props.counter.id}</h4>
        Wymień: {this.s2f(this.props.counter.typeIn)} na{" "}
        {this.s2f(this.props.counter.typeOut) + " "} otrzymasz
        <span className={this.getBadgeClasses()}> {this.calc()}</span>
        za
        <input
          type="numeric"
          value={this.state.value}
          onChange={this.handleChange}
        />
        <button
          onClick={() => this.props.onBuy(this.props.counter.id)}
          className="btn btn-primary btn-sm ml-2"
        >
          Kupuję
        </button>
        (Kurs wnosi:
        {Math.floor(this.props.counter.course * 0.95 * 1000) / 1000})
      </div>
    );
  }

  calc() {
    const { course } = this.props.counter;
    return Math.floor(this.state.value * course * 0.95);
  }

  handleChange(event) {
    this.setState({ value: event.target.value });
  }

  eCoin() {
    if (this.props.counter.typeIn === "BC") return this.props.user.BC;
    if (this.props.counter.typeIn === "LC") return this.props.user.LC;
    if (this.props.counter.typeIn === "ET") return this.props.user.ET;
    return 0;
  }

  /* helper methods */
  getBadgeClasses() {
    let classes = "badge m-2 badge-";
    classes +=
      this.calc() === 0
        ? "warning"
        : this.eCoin() < this.state.value
        ? "danger"
        : this.calc() < this.props.counter.mintran
        ? "danger"
        : this.calc() > this.props.counter.amount * 0.95
        ? "danger"
        : this.calc() > 0
        ? "primary"
        : 0;
    return classes;
  }
  s2f(s) {
    // object destructuring to read the value property of the 'state';
    return s === "BC" ? "BitCoin" : "LC" ? "Litecoin" : "ET" ? "Etherum" : 0;
  }
}

export default Counter;
