import * as apiCalls from '../api/apiCalls';
import {createStore} from 'redux';
import authenticationReducer from './authenticationReducer';

const notLoggedInUser = {
    isLoggedIn: false,
    id: 0,
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    image: '',
    password: ''
}


const setupStore = () => {
    let loadedUserFromLocalStorage = checkIfUserDataAreStoredInLocalStorage();
    const state = loadedUserFromLocalStorage ? loadedUserFromLocalStorage : {...notLoggedInUser};

    const store = createStore(authenticationReducer, state,
        window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

    store.subscribe(() => {
        apiCalls.setAuthHeader(store.getState);
        localStorage.setItem('noticeboard-user', JSON.stringify(store.getState()));
    });

    return store;
};

export default setupStore;

const checkIfUserDataAreStoredInLocalStorage = () => {
    let loadedUserFromLocalStorage = localStorage.getItem('noticeboard-user');
    if (loadedUserFromLocalStorage) {
        try {
            loadedUserFromLocalStorage = JSON.parse(loadedUserFromLocalStorage);
            apiCalls.setAuthHeader(loadedUserFromLocalStorage);
        } catch {
            loadedUserFromLocalStorage = null;
        }
    }
    return loadedUserFromLocalStorage;
}

