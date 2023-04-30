import React, { useState } from 'react';
import { withRouter } from 'react-router-dom';

import '../../App.css';


function SignInPage(props) { // Aquí se pasa props como parámetro
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch('http://localhost:8000/api/token/', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username: email, password: password }),
    });
    const data = await response.json();
    console.log("data: ", data)

    if (response.ok) {
      localStorage.setItem('token', data.access);
      props.history.push("/home")
    } else {
      console.log('Login failed');
      alert(data.detail)
    }
  };

  return (
    <div className="text-center m-5-auto">
      <h2>Sign in to us</h2>
      <form onSubmit={handleSubmit}>
        <p>
          <label>Username or email address</label><br />
          <input type="text" name="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </p>
        <p>
          <label>Password</label>
          <br />
          <input type="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </p>
        <p>
          <button id="sub_btn" type="submit">Login</button>
        </p>
      </form>
    </div>
  );
}

export default withRouter(SignInPage);
