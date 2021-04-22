import axios from 'axios';

export const setAuthHeader = ({isLoggedIn, username, password}) => {
    if (!isLoggedIn) {
        delete axios.defaults.headers.common['Authorization'];
    } else {
        axios.defaults.headers.common['Authorization'] = `Basic ${btoa(username + ":" + password)}`;
    }
}


export const register = user => {
    return axios.post('/api/1.0/users', user);
};

export let getUser = username => {
    return axios.get(`/api/1.0/users/${username}`);
}

export const login = user => {
    return axios.post('/api/1.0/login', {}, {auth: user});
}