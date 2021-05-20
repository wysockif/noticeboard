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

export const putNotice = (noticeId, notice) => {
    return axios.put(apiUrl + '/notices/' + noticeId, notice);
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

export const getNoticesByUsername = (username, page = 0) => {
    return axios.get(apiUrl + `/notices?username=${username}&page=${page}&sort=createdAt,desc`);
}

export const login = user => {
    return axios.post(apiUrl + '/login', {}, {auth: user});
}

export const activateAccount = (email) => {
    return axios.patch(apiUrl + `/users/${email}/activation`);
}


export const verifyToken = (token) => {
    return axios.post(apiUrl + `/tokens/${token}`);
}

export const getNotices = (page = 0, sort = 'createdAt,desc', size = 12, gettingNoticesParams) => {
    let url = apiUrl + `/notices?page=${page}&sort=${sort}&size=${size}`;
    if (gettingNoticesParams) {
        url += mapToUrl(gettingNoticesParams);
    }
    return axios.get(url);
}

function mapToUrl(gettingNoticesParams) {
    let url = '';
    if (gettingNoticesParams.minPriceParam) {
        url += `&minPrice=${gettingNoticesParams.minPriceParam}`
    }
    if (gettingNoticesParams.maxPriceParam) {
        url += `&maxPrice=${gettingNoticesParams.maxPriceParam}`
    }
    if (gettingNoticesParams.locationParam) {
        url += `&location=${gettingNoticesParams.locationParam}`
    }
    if (gettingNoticesParams.searchingParam) {
        url += `&searched=${gettingNoticesParams.searchingParam}`
    }
    return url;
}
