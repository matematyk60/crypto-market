import React, { Component } from "react";
import Nick from "./nick";
import Password from "./password";
import Email from "./email";

class Singin extends Component {
  constructor(props) {
    super(props);
    this.state = { password1: "", password2: "" };
  }
  render() {
    const { email, password } = this.props;
    return (
      <div>
        <div>
          {" "}
          Podaj email: <Email email={email} />
        </div>
        <div>
          {" "}
          Podaj hasło: <Password password={this.state.password1} />
        </div>
        <div>
          {" "}
          Powtórz hasło: <Password password={this.state.password2} />
        </div>
        {this.passwordOk()}
      </div>
    );
  }
  passwordOk() {
    if (this.state.password1 == this.state.password2) {
      this.props.password = this.state.password1;
    } else {
      return "Hasła nie są takie same";
    }
  }
}

export default Singin;
