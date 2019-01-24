import React, { Component } from "react";
import NavBar from "./components/navbar";
import "./App.css";
import Counters from "./components/counters";
import axios from "axios";

class App extends Component {
  state = {
    counters: [
      {
        id: 1,
        course: 3,
        mintran: 3,
        amount: 500,
        typeIn: "BC",
        typeOut: "LC",
        value: 0,
        user: 3
      },
      {
        id: 2,
        course: 3,
        mintran: 3,
        amount: 500,
        typeIn: "BC",
        typeOut: "LC",
        value: 0,
        user: 2
      },
      {
        id: 5,
        course: 3,
        mintran: 3,
        amount: 500,
        typeIn: "LC",
        typeOut: "BC",
        value: 0,
        user: 3
      }
    ],
    user: {
      id: 98,
      BC: 78,
      LC: 4
    }
  };

  // 'constructor' (1st) hook of the MOUNTing phase
  constructor(props) {
    super(props);
    console.log("App - Constructor", this.props);
    // this.state = this.props.something.... // can be set state directly to the props here
  }

  // 'render' (2nd) hook of the MOUNTing phase
  render() {
    console.log("App - Rendered"); // along with all its children recursively
    return (
      <React.Fragment>
        <NavBar
          totalCounters={this.state.counters.length}
          onReset={this.handleReset}
          user={this.state.user}
        />
        <main>{this.logged(this.props)}</main>
      </React.Fragment>
    );
  }

  // 'componentDidMount' (3rd) hook of the MOUNTing phase
  componentDidMount() {
    // perfect place to make AJAX call to get data from the server
    console.log("App - Mounted");
    // this.setState(to the recieved data...)
  }

  /* Children's event handlers */
  handleDecrement = counter => {
    const counters = [...this.state.counters];
    const index = counters.indexOf(counter);
    counters[index].value--;
    this.setState({ counters });
  };
  handleIncrement = counter => {
    const counters = [...this.state.counters];
    const index = counters.indexOf(counter);
    counters[index].value++;
    this.setState({ counters });
  };
  handleDelete = counterId => {
    // return all the counter objects except the one with the given id (the del btn of which is clicked)
    const counters = this.state.counters.filter(c => c.id !== counterId);
    this.setState({ counters }); // polluting the state of the counters
  };
  handleReset = () => {
    const counters = this.state.counters.map(c => {
      c.value = 0;
      return c;
    });
    this.setState({ counters });
  };
  handleBuy = () => {};

  logged(props) {
    return (
      <Counters
        user={this.state.user}
        counters={this.state.counters}
        onDecrement={this.handleDecrement}
        onIncrement={this.handleIncrement}
        onBuy={this.handleBuy}
      />
    );
  }

  mo = () => {
    // return all the counter objects except the one with the given id (the del btn of which is clicked)
    const counters = this.state.counters.filter(
      c => c.user == this.state.user.id
    );
    return counters;
  };
}
export default App;
