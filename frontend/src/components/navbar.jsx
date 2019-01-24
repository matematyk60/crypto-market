import React from "react";

let style = {
  resetBtn: {
    position: "fixed",
    top: "10px",
    left: "15%"
  }
};

// Stateless Functional Component
const NavBar = ({ totalCounters, onReset, user }) => {
  // 'Object destructuring' applied
  console.log("NavBar - Rendered");

  return (
    <nav className="navbar navbar-light bg-light">
      <span className="navbar-brand">
        Ilość ofert{" "}
        <span className="badge badge-pill badge-secondary">
          {totalCounters}
        </span>
      </span>
      <span>
        <span className="badge badge-pill badge-secondary">
          BitCion:{user.BC}
        </span>
        <span className="badge badge-pill badge-secondary">
          Litecoin:{user.LC}
        </span>
        <span className="badge badge-pill badge-secondary">
          Etherum:{user.ET}
        </span>
        {"  "}
        <span className="badge badge-pill badge-secondary">Konto</span>
        <span className="badge badge-pill badge-secondary">Wyloguj</span>
      </span>
    </nav>
  );
};

export default NavBar;
