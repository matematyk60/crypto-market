import React, { Component } from "react";

class Email extends Component {
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
        placeholder="email"
      />
    );
  }
}

export default Email;
