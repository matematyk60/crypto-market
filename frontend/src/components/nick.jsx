import React, { Component } from "react";

class Nick extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: ""
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
    if (prevProps.value === this.props.value) {
      console.log("didn't updated!!!");
    }
  }

  componentWillUnmount() {
    console.log("Component - Unmount");
  }

  render() {
    console.log("text - Rendered");
    return (
      <input
        type="text"
        value={this.state.value}
        onChange={this.handleChange}
        placeholder="nick"
      />
    );
  }
}

export default Nick;
