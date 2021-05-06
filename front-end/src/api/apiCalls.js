import axios from 'axios';

const apiUrl = '/api/1.0';


export const setAuthHeader = ({isLoggedIn, username, password}) => {
    if (isLoggedIn) {
        axios.defaults.headers.common['Authorization'] = `Basic ${btoa(username + ":" + password)}`;
    } else {
        delete axios.defaults.headers.common['Authorization'];
    }
}


export const updateUser = (id, body) => {
    return axios.patch(apiUrl + `/users/${id}`, body);
}


export const register = user => {
    return axios.post(apiUrl + '/users', user);
};

export let getUser = username => {
    return axios.get(apiUrl + `/users/${username}`);
}

export const postNotice = notice => {
    return axios.post(apiUrl + '/notices', notice);
}

export const getNotice = id => {
    return axios.get(apiUrl + '/notices/' + id);
}

export const deleteNotice = id => {
    return axios.delete(apiUrl + '/notices/' + id);
}

export const getUserByNoticeId = noticeId => {
    return axios.get(apiUrl + '/users/notice/' + noticeId);
}

export const getNoticesByUsername = username => {
    return axios.get(apiUrl + `/notices?username=${username}&sort=createdAt,desc`);
}

export const getNotices = () => {
    return axios.get(apiUrl + `/notices?sort=createdAt,desc`);
}


export const login = user => {
    return axios.post(apiUrl + '/login', {}, {auth: user});
}
