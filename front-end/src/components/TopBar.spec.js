import React from 'react';
import { render } from '@testing-library/react';
import TopBar from './TopBar';
import { MemoryRouter } from 'react-router';
import authenticationReducer from '../redux/authenticationReducer';
import { Provider } from 'react-redux';
import { createStore } from 'redux';

const userNotLoggedInState = {
    isLoggedIn: false,
    id: 0,
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    image: '',
    password: ''
}

const userLoggedInState = {
    isLoggedIn: true,
    id: 5,
    username: 'user5',
    firstName: 'first5',
    lastName: 'last5',
    email: 'email5@mail.com',
    image: 'profile.png',
    password: 'Password123'
}
let store;

const renderTopbar = (state = userNotLoggedInState) => {
    store = createStore(authenticationReducer, state);
    return render(
        <Provider store={store}>
            <MemoryRouter>
                <TopBar />
            </MemoryRouter>
        </Provider>
    );
};

describe('TopBar', () => {
    describe('Layout', () => {

        it('has link to home', () => {
            // given
            const { container } = renderTopbar();
            // when
            const image = container.querySelector('img');
            // then
            const link = image.parentElement;
            expect(link).toHaveAttribute('href', '/')
        });

        it('has app logo', () => {
            // given
            const { container } = renderTopbar();
            // when
            const image = container.querySelector('img');
            // then
            expect(image.src).toContain('logo.png');
        });

        it('has link to register', () => {
            // given
            const { queryByText } = renderTopbar();
            // when
            const registerLink = queryByText('Rejestracja');
            // then
            expect(registerLink.getAttribute('href')).toBe('/register')
        });

        it('has link to login', () => {
            // given
            const { queryByText } = renderTopbar();
            // when
            const loginLink = queryByText('Logowanie');
            // then
            expect(loginLink.getAttribute('href')).toBe('/login')
        });

        it('has link to user profile when user logged in', () => {
            // given
            const { queryByText } = renderTopbar(userLoggedInState);
            // when
            const profileLink = queryByText('Moja tablica');
            // then
            expect(profileLink.getAttribute('href')).toBe('/user/' + userLoggedInState.username);
        });

        it('has link to logout when user logged in', () => {
            // given
            const { queryByText } = renderTopbar(userLoggedInState);
            // when
            const logoutLink = queryByText('Wyloguj się');
            // then
            expect(logoutLink).toBeInTheDocument();
        });
    });
});