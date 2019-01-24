import React, { Component } from "react";
import Nick from "./nick";
import Password from "./password";

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      password: ""
    };
  }
  render() {
    return (
      <form className="form-signin">
        <div>
          {" "}
          <div>
            {" "}
            <Nick />
          </div>
          <div>
            {" "}
            <Password />
          </div>
          <button name="Login">Login</button>
          <button name="PasswordRecover">odzyskaj hasło</button>
          <div>
            <button name="Singin">Załóż konto</button>
          </div>
        </div>
      </form>
    );
  }
}

export default Login;
