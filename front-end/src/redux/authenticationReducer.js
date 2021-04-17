const initialState = {
    isLoggedIn: false,
    id: 0,
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    image: '',
    password: ''
}


export default function authenticationReducer(state = initialState, action) {
    switch (action.type) {
        case 'LOGOUT_SUCCESS':
            return { ...initialState };
        default:
            return state;
    }
}