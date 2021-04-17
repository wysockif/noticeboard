import React from 'react';
import ReactDOM from 'react-dom';
import App from './containers/App';
import reportWebVitals from './reportWebVitals';
import { HashRouter } from 'react-router-dom';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/js/all.js';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import authenticationReducer from './redux/authenticationReducer';


const userLoggedInState = {
  isLoggedIn: true,
  id: 5,
  username: 'user5',
  firstName: 'first5',
  lastName: 'last5',
  email: 'email5@mail.com',
  image: 'profile.png',
  password: 'Password123'
}
const store = createStore(authenticationReducer, userLoggedInState);

ReactDOM.render(
  <React.StrictMode>
    <Provider store={store}>
      <HashRouter>
        <App />
      </HashRouter>
    </Provider>
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();
