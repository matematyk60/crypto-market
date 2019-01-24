import React, { Component } from "react";
import MyOffer from "./myoffer";

class MyOffers extends Component {
  render() {
    console.log("Counters - Rendered");
    // 'Object destructuring' applied
    const { user, counters, onDecrement, onIncrement, onDelete } = this.props;
    return (
      <div>
        {counters.map(counter => (
          <MyOffer
            key={counter.id}
            onDecrement={onDecrement}
            onIncrement={onIncrement}
            onDelete={onDelete}
            counter={counter}
            user={user} // this 'counter' object encapsulates all the props / data within it and save us from redundant explicit prop declaration
          />
        ))}
      </div>
    );
  }
}

export default MyOffers;
