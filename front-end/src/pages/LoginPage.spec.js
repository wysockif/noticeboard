import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import { LoginPage } from './LoginPage';
import '@testing-library/jest-dom/extend-expect';



describe('LoginPage', () => {
    describe('Layout', () => {

        it('has login header', () => {
            // given
            const { container } = render(<LoginPage />);
            // when
            const header = container.querySelector('h1');
            // then
            expect(header).toHaveTextContent('Zaloguj się');
        });

        it('has input for username', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            // when
            const input = queryByPlaceholderText('Nazwa użytkownika')
            // then
            expect(input).toBeInTheDocument();
        });

        it('has input for password', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            // when
            const input = queryByPlaceholderText('Hasło')
            // then
            expect(input).toBeInTheDocument();
        });

        it('has password type for input for password', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            // when
            const passwordInput = queryByPlaceholderText('Hasło');
            // then
            expect(passwordInput.type).toBe('password');
        });


        it('has login button', () => {
            // given
            const { container } = render(<LoginPage />);
            // when
            const header = container.querySelector('button');
            // then
            expect(header).toHaveTextContent('Zaloguj się');
        });

    });

    describe('Interactions', () => {
        const changeEvent = content => {
            return {
                target: {
                    value: content
                }
            };
        }
        it('sets the username value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            const usernameInput = queryByPlaceholderText('Nazwa użytkownika')
            // when
            fireEvent.change(usernameInput, changeEvent('newusername'));
            // then
            expect(usernameInput).toHaveValue('newusername');
        });

        it('sets the password value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            const passwordInput = queryByPlaceholderText('Hasło')
            // when
            fireEvent.change(passwordInput, changeEvent('newPassword123'));
            // then
            expect(passwordInput).toHaveValue('newPassword123');
        });
    });
});

console.error = () => { }