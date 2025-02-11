// src/components/SignUpContainer.jsx
import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import '../theme/FormContainer.css';

const SignUpContainer = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleSignUp = () => {
    // Get input values from the DOM; you might also consider using component state.
    const username = document.querySelector('input[placeholder="Username"]').value;
    const email = document.querySelector('input[placeholder="Email"]').value;
    const password = document.querySelector('input[placeholder="Password"]').value;

    // Get the registered users from localStorage or initialize with an empty array.
    const storedUsers = JSON.parse(localStorage.getItem("users")) || [];

    // Check for duplicates (simple check by username or email)
    const userExists = storedUsers.some(
      (user) => user.username === username || user.email === email
    );

    if (userExists) {
      alert("Registration failed: A user with that username or email already exists.");
      return;
    }

    // Create a new user object
    const newUser = { username, email, password };

    // Add the new user to the existing users array
    const updatedUsers = [...storedUsers, newUser];
    localStorage.setItem("users", JSON.stringify(updatedUsers));

    alert("Registration successful! Please log in now.");
    navigate("/"); // Navigate to login after successful registration.
  };

  return (
    <div className="form-container">
      <div className="col col-1">
        <div className="image-layer">
          <img src="src/assets/white-outline.png" className="form-image-main" alt="Main" />
          <img src="src/assets/dots.png" className="form-image dots" alt="Dots" />
          <img src="src/assets/coin.png" className="form-image coin" alt="Coin" />
          <img src="src/assets/spring.png" className="form-image spring" alt="Spring" />
          <img src="src/assets/rocket.png" className="form-image rocket" alt="Rocket" />
          <img src="src/assets/cloud.png" className="form-image cloud" alt="Cloud" />
        </div>
        <p className="featured-words">Join Us to Unlock Your Full Potential!</p>
      </div>
      <div className="col col-2">
        <div className="btn-box">
          <button
            className={`btn btn-1 ${location.pathname === "/" ? "active" : ""}`}
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
            <span>Sign Up</span>
          </div>
        </div>
        <div className="form-inputs">
          <div className="input-box">
            <input type="text" className="input-field" placeholder="Username" required />
            <i className="bx bx-user icon"></i>
          </div>
          <div className="input-box">
            <input type="email" className="input-field" placeholder="Email" required />
            <i className="bx bx-envelope icon"></i>
          </div>
          <div className="input-box">
            <input type="password" className="input-field" placeholder="Password" required />
            <i className="bx bx-lock-alt icon"></i>
          </div>
          <div className="input-box">
            <button type="button" className="input-submit" onClick={handleSignUp}>
              <span>Sign Up</span>
              <i className="bx bx-right-arrow-alt"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SignUpContainer;
