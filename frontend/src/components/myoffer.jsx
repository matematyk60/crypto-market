import React, { Component } from "react";
class MyOffer extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return (
      <div>
        {"Oferujesz wymianę: " +
          this.props.counter.amount +
          " " +
          this.s2f(this.props.counter.typeOut) +
          " na " +
          this.s2f(this.props.counter.typeIn) +
          " po kursie: " +
          this.props.counter.course +
          " minmalna ilość wymienianej wlauty, to " +
          this.props.counter.mintran +
          " numer oferty, to " +
          this.props.counter.id}
      </div>
    );
  }

  s2f(s) {
    // object destructuring to read the value property of the ' props';
    return s === "BC" ? "BitCoin" : "LC" ? "Litecoin" : "ET" ? "Etherum" : 0;
  }
}

export default MyOffer;
