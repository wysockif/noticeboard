import axios from 'axios';

export const register = user => {
    return axios.post('/api/1.0/users', user);
};

export const login = user => {
    return axios.post('/api/1.0/login', {}, { auth: user });
}