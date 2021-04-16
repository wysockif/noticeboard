import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import App from './App';

const createApp = url => {
    return render(
        <MemoryRouter initialEntries={[url]}>
            <App />
        </MemoryRouter>
    );
};

describe('App', () => {
    it('displays homepage when url is /', () => {
        // given
        const url = '/'
        // when
        const { queryByTestId } = createApp(url);
        // then
        const homeDiv = queryByTestId('homepage');
        expect(homeDiv).toBeInTheDocument();
    });

    it('displays login page when url is /login', () => {
        // given
        const url = '/login'
        // when
        const { container } = createApp(url);
        // then
        const header = container.querySelector('h1');
        expect(header).toHaveTextContent('Zaloguj się');
    });

    it('displays only pogin page when url is /login', () => {
        // given
        const url = '/login'
        // when
        const { queryByTestId } = createApp(url);
        // then
        const homeDiv = queryByTestId('homepage');
        expect(homeDiv).not.toBeInTheDocument();
    });
    it('displays registeration page when url is /register', () => {
        // given
        const url = '/register'
        // when
        const { container } = createApp('/register');
        // then
        const header = container.querySelector('h1');
        expect(header).toHaveTextContent('Zarejestruj się');
    });
});