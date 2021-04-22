import React from 'react';
import axios from 'axios';
import authenticationReducer from '../redux/authenticationReducer';
import {Provider} from 'react-redux';
import {createStore} from 'redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import App from './App';
import * as apiCalls from '../api/apiCalls'

beforeEach(() => {
    delete axios.defaults.headers.common['Authorization'];
    localStorage.clear();
    apiCalls.getUser = jest.fn().mockResolvedValue({
        data: {
            id: 4,
            firstName: 'First',
            lastName: 'Last',
            email: 'email@mail.com',
            username: 'username1',
            image: 'testImage.png'
        }
    });
});

let store;
const renderAppComponent = url => {
    store = createStore(authenticationReducer);
    return render(
        <Provider store={store}>
            <MemoryRouter initialEntries={[url]}>
                <App/>
            </MemoryRouter>
        </Provider>
    );
};

describe('App', () => {
    it('displays homepage when url is /', () => {
        // given
        const url = '/';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const homeDiv = queryByTestId('homepage');
        expect(homeDiv).toBeInTheDocument();
    });

    it('displays login page when url is /login', () => {
        // given
        const url = '/login';
        // when
        const {container} = renderAppComponent(url);
        // then
        const header = container.querySelector('h1');
        expect(header).toHaveTextContent('Zaloguj się');
    });

    it('displays only login page when url is /login', () => {
        // given
        const url = '/login';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const loginDiv = queryByTestId('login');
        expect(loginDiv).not.toBeInTheDocument();
    });

    it('displays registration page when url is /register', () => {
        // given
        const url = '/register';
        // when
        const {container} = renderAppComponent(url);
        // then
        const header = container.querySelector('h1');
        expect(header).toHaveTextContent('Zarejestruj się');
    });

    it('displays user profile page when url is /user/:username', () => {
        // given
        const url = '/user/username1';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const userProfileDiv = queryByTestId('userprofilepage');
        expect(userProfileDiv).toBeInTheDocument();
    });

    it('displays notice page when url is /notice/:noticeid', () => {
        // given
        const url = '/notice/123';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const noticePageDiv = queryByTestId('noticepage');
        expect(noticePageDiv).toBeInTheDocument();
    });

    it('displays create notice page when url is /notice/new', () => {
        // given
        const url = '/notice/new';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const noticeFormPageDiv = queryByTestId('createnoticepage');
        expect(noticeFormPageDiv).toBeInTheDocument();
    });

    it('displays edit notice page when url is /notice/edit/123', () => {
        // given
        const url = '/notice/edit/123';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const noticeFormPageDiv = queryByTestId('editnoticepage');
        expect(noticeFormPageDiv).toBeInTheDocument();
    });

    it('displays error page when url is diffrent', () => {
        // given
        const url = '/asfdsd';
        // when
        const {queryByTestId} = renderAppComponent(url);
        // then
        const errorPageDiv = queryByTestId('errorpage');
        expect(errorPageDiv).toBeInTheDocument();
    });
});