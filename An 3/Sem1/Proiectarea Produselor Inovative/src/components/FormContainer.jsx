// src/components/FormContainer.jsx

import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../theme/FormContainer.css";

const FormContainer = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogin = () => {
    // Get input values directly from the DOM using querySelector
    const username = document.querySelector('input[placeholder="Username"]').value;
    const password = document.querySelector('input[placeholder="Password"]').value;

    // Check if fields are non-empty (you can add additional validation)
    if (!username || !password) {
      alert("Please enter both username and password.");
      return;
    }

    // Simulate a login process without checking against registered users.
    // A dummy token is generated (or you can use a constant dummy token)
    const dummyToken = "dummy-access-token";
    localStorage.setItem("token", dummyToken);

    // Navigate to the main page after "successful" login
    navigate("/main");
  };

  const handleSignUp = () => {
    navigate("/signup");
  };

  return (
    <div className="form-container">
      <div className="col col-1">
        <div className="image-layer">
          <img src="/white-outline.png" className="form-image-main" alt="Main" />
          <img src="/dots.png" className="form-image dots" alt="Dots" />
          <img src="/coin.png" className="form-image coin" alt="Coin" />
          <img src="/spring.png" className="form-image spring" alt="Spring" />
          <img src="/rocket.png" className="form-image rocket" alt="Rocket" />
          <img src="/cloud.png" className="form-image cloud" alt="Cloud" />
        </div>
        <p className="featured-words">You Are Few Minutes Away to Boost Your Skills</p>
      </div>
      <div className="col col-2">
        <div className="btn-box">
          <button
            className={`btn btn-1 ${location.pathname === "/" ? "active" : ""}`}
            id="login"
            onClick={() => navigate("/")}
          >
            Sign In
          </button>
          <button
            className={`btn btn-2 ${location.pathname === "/signup" ? "active" : ""}`}
            id="Register"
            onClick={handleSignUp}
          >
            Sign Up
          </button>
        </div>

        <div className="login-form">
          <div className="form-title">
            <span>Sign In</span>
          </div>
        </div>
        <div className="form-inputs">
          <div className="input-box">
            <input type="text" className="input-field" placeholder="Username" required />
            <i className="bx bx-user icon"></i>
          </div>
          <div className="input-box">
            <input type="password" className="input-field" placeholder="Password" required />
            <i className="bx bx-lock-alt icon"></i>
          </div>
          <div className="forgot-pass">
            <a href="#">Forgot Password?</a>
          </div>
          <div className="input-box">
            <button type="button" className="input-submit" onClick={handleLogin}>
              <span>Sign In</span>
              <i className="bx bx-right-arrow-alt"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FormContainer;
