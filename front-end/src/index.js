import React from 'react';
import ReactDOM from 'react-dom';
import App from './containers/App';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { HashRouter } from 'react-router-dom';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/js/all.js';
import { Provider } from 'react-redux';
import setupStore from './redux/configureStore';

const store = setupStore();

ReactDOM.render(
  <Provider store={store}>
    <HashRouter>
      <App />
    </HashRouter>
  </Provider>,
  document.getElementById('root')
);

reportWebVitals();
