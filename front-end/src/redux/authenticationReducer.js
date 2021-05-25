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
        case 'LOGIN_SUCCESS':
            return {...action.payload};
        case 'LOGOUT_SUCCESS':
            return {...initialState};
        case 'CHANGE_PASSWORD_SUCCESS':
            return {...state, password: action.payload.password}
        default:
            return state;
    }
}