import React from 'react';
import ReactDOM from 'react-dom';
import App from './containers/App';
import './index.css';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter} from 'react-router-dom';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/js/all.js';
import {Provider} from 'react-redux';
import setupStore from './redux/configureStore';

const store = setupStore();

ReactDOM.render(
    <Provider store={store}>
        <BrowserRouter>
            <App/>
        </BrowserRouter>
    </Provider>,
    document.getElementById('root')
);

reportWebVitals();
