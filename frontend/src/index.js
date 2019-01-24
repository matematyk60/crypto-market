import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import App from "./App";
import Login from "./Login";
import MyOffers from "./myOffers";
import registerServiceWorker from "./registerServiceWorker";
import "bootstrap/dist/css/bootstrap.css";
import Pager from "react-subpage";

const urlMap = {
  "/": App,
  "/login": Login,
  "/myOffers": MyOffers
};
const pager = new Pager(urlMap);

ReactDOM.render(pager.element, document.getElementById("root"));
registerServiceWorker();
