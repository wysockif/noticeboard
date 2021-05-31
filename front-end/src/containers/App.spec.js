import React from 'react';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import App from './App';
import * as apiCalls from '../api/apiCalls'
import configureStore from "../redux/configureStore";

beforeEach(() => {
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
    store = configureStore();
    return render(
        <Provider store={store}>
            <MemoryRouter initialEntries={[url]}>
                <App/>
            </MemoryRouter>
        </Provider>
    );

};

const setUser1LoggedInStorage = () => {
    localStorage.setItem('noticeboard-user', JSON.stringify({
        id: 1,
        username: 'user1',
        displayName: 'display1',
        image: 'profile1.png',
        password: 'Password123',
        isLoggedIn: true
    }));
};

describe('App', () => {
    // it('displays homepage when url is /', () => {
    //     // given
    //     const url = '/';
    //     // when
    //     const {queryByTestId} = renderAppComponent(url);
    //     // then
    //     const homeDiv = queryByTestId('homepage');
    //     expect(homeDiv).toBeInTheDocument();
    // });

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



    it('displays create notice page when url is /notice/new', () => {
        // given
        setUser1LoggedInStorage();
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

console.error = () => {}