import React from 'react';
import axios from 'axios';
import authenticationReducer from '../redux/authenticationReducer';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import App from './App';

const changeEvent = content => {
    return {
        target: {
            value: content
        }
    };
};

let store;
const renderAppComponent = url => {
    store = createStore(authenticationReducer);
    return render(
        <Provider store={store}>
            <MemoryRouter initialEntries={[url]}>
                <App />
            </MemoryRouter>
        </Provider>
    );
};

describe('App', () => {
    it('displays homepage when url is /', () => {
        // given
        const url = '/'
        // when
        const { queryByTestId } = renderAppComponent(url);
        // then
        const homeDiv = queryByTestId('homepage');
        expect(homeDiv).toBeInTheDocument();
    });

    it('displays login page when url is /login', () => {
        // given
        const url = '/login'
        // when
        const { container } = renderAppComponent(url);
        // then
        const header = container.querySelector('h1');
        expect(header).toHaveTextContent('Zaloguj się');
    });

    it('displays only login page when url is /login', () => {
        // given
        const url = '/login'
        // when
        const { queryByTestId } = renderAppComponent(url);
        // then
        const loginDiv = queryByTestId('login');
        expect(loginDiv).not.toBeInTheDocument();
    });

    it('displays registration page when url is /register', () => {
        // given
        const url = '/register'
        // when
        const { container } = renderAppComponent(url);
        // then
        const header = container.querySelector('h1');
        expect(header).toHaveTextContent('Zarejestruj się');
    });

    it('displays user profile page when url is /user/:username', () => {
        // given
        const url = '/user/:username1'
        // when
        const { queryByTestId } = renderAppComponent(url);
        // then
        const userProfileDiv = queryByTestId('userprofilepage');
        expect(userProfileDiv).toBeInTheDocument();
    });

    it('displays notice page when url is /notice/:noticeid', () => {
        // given
        const url = '/notice/:123'
        // when
        const { queryByTestId } = renderAppComponent(url);
        // then
        const noticePageDiv = queryByTestId('noticepage');
        expect(noticePageDiv).toBeInTheDocument();
    });

    it('displays notice form page when url is /mynotice/:noticeid', () => {
        // given
        const url = '/mynotice/:123'
        // when
        const { queryByTestId } = renderAppComponent(url);
        // then
        const noticeFormPageDiv = queryByTestId('noticeformpage');
        expect(noticeFormPageDiv).toBeInTheDocument();
    });

    it('displays error page when url is diffrent', () => {
        // given
        const url = '/asfdsd'
        // when
        const { queryByTestId } = renderAppComponent(url);
        // then
        const errorPageDiv = queryByTestId('errorpage');
        expect(errorPageDiv).toBeInTheDocument();
    });


});