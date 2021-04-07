import axios from 'axios';

export const register = user => {
    return axios.post('/api/1.0/users', user);
};