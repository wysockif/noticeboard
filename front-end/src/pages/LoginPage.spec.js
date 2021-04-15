import React from 'react';
import { render, fireEvent, waitFor, findByText } from '@testing-library/react';
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
        let usernameInput, passwordInput, button;

        const setup = props => {
            const rendered = render(<LoginPage {...props} />);
            const { container, queryByPlaceholderText } = rendered;
            usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            passwordInput = queryByPlaceholderText('Hasło');
            button = container.querySelector('button');
            fireEvent.change(usernameInput, changeEvent('newusername'));
            fireEvent.change(passwordInput, changeEvent('newPassword123'));
            return rendered;
        }
        it('sets the username value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            const usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            // when
            fireEvent.change(usernameInput, changeEvent('newusername'));
            // then
            expect(usernameInput).toHaveValue('newusername');
        });

        it('sets the password value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            const passwordInput = queryByPlaceholderText('Hasło');
            // when
            fireEvent.change(passwordInput, changeEvent('newPassword123'));
            // then
            expect(passwordInput).toHaveValue('newPassword123');
        });

        it('calls postLogin when input fields are not empty', () => {
            // given
            const actions = {
                postLogin: jest.fn().mockResolvedValue({})
            }
            setup({ actions });
            // when
            fireEvent.click(button);
            // then
            expect(actions.postLogin).toHaveBeenCalledTimes(1);
        });

        it('calls postLogin with username & password', () => {
            // given
            const actions = {
                postLogin: jest.fn().mockResolvedValue({})
            }
            setup({ actions });
            // when
            fireEvent.click(button);
            // then
            const expectedBody = {
                username: 'newusername',
                password: 'newPassword123'
            }
            expect(actions.postLogin).toHaveBeenCalledWith(expectedBody);
        });

        it('displays message when login fails', async () => {
            // given
            const actions = {
                postLogin: jest.fn().mockRejectedValue({
                    response: {
                        data: {
                            message: 'Login failed'
                        }
                    }
                })
            }
            const { queryByText, findByText } = setup({ actions });
            // when
            fireEvent.click(button);
            // then
            await waitFor(() => findByText('Login failed'));
            const message = queryByText('Login failed');
            expect(message).toBeInTheDocument();
        });

        it('does not throw error when actions not provided in props', () => {
            // given
            const { container, queryByPlaceholderText } = setup();
            // when
            const clicking = () => fireEvent.click(button);
            // then
            expect(clicking).not.toThrow();
        });

        it('enables the button when username and password is not empty', () => {
            // given
            setup();
            // when
            fireEvent.change(usernameInput, changeEvent('notEmptyUsername'));
            fireEvent.change(passwordInput, changeEvent('notEmptyPassword123'));
            // then
            expect(button).not.toBeDisabled();
        });

        it('disables the button when username is empty', () => {
            // given
            setup();
            // when
            fireEvent.change(usernameInput, changeEvent(''));
            fireEvent.change(passwordInput, changeEvent('notEmptyPassword123'));
            // then
            expect(button).toBeDisabled();
        });

        it('disables the button when password is empty', () => {
            // given
            setup();
            // when
            fireEvent.change(usernameInput, changeEvent('notEmptyUsername'));
            fireEvent.change(passwordInput, changeEvent(''));
            // then
            expect(button).toBeDisabled();
        });

 
    });
});

console.error = () => { }