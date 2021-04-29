import React from 'react';
import {fireEvent, render, waitFor, waitForElementToBeRemoved} from '@testing-library/react';
import {LoginPage} from './LoginPage';


describe('LoginPage', () => {
    describe('Layout', () => {
        it('has login header', () => {
            // given
            const {container} = render(<LoginPage/>);
            // when
            const header = container.querySelector('h1');
            // then
            expect(header).toHaveTextContent('Zaloguj się');
        });

        it('has input for username', () => {
            // given
            const {queryByPlaceholderText} = render(<LoginPage/>);
            // when
            const input = queryByPlaceholderText('Nazwa użytkownika')
            // then
            expect(input).toBeInTheDocument();
        });

        it('has input for password', () => {
            // given
            const {queryByPlaceholderText} = render(<LoginPage/>);
            // when
            const input = queryByPlaceholderText('Hasło')
            // then
            expect(input).toBeInTheDocument();
        });

        it('has password type for input for password', () => {
            // given
            const {queryByPlaceholderText} = render(<LoginPage/>);
            // when
            const passwordInput = queryByPlaceholderText('Hasło');
            // then
            expect(passwordInput.type).toBe('password');
        });


        it('has login button', () => {
            // given
            const {container} = render(<LoginPage/>);
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
        };
        let usernameInput, passwordInput, button;

        const renderLoginPage = props => {
            const rendered = render(<LoginPage {...props} />);
            const {container, queryByPlaceholderText} = rendered;
            usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            passwordInput = queryByPlaceholderText('Hasło');
            button = container.querySelector('button');
            fireEvent.change(usernameInput, changeEvent('newusername'));
            fireEvent.change(passwordInput, changeEvent('newPassword123'));
            return rendered;
        }

        const mockAsyncDelayed = () => {
            return jest.fn().mockImplementation(() => {
                return new Promise((resolve, reject) => {
                    setTimeout(() => resolve({}), 300);
                });
            });
        }

        it('sets the username value into state', () => {
            // given
            const {queryByPlaceholderText} = render(<LoginPage/>);
            const usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            // when
            fireEvent.change(usernameInput, changeEvent('newusername'));
            // then
            expect(usernameInput).toHaveValue('newusername');
        });

        it('sets the password value into state', () => {
            // given
            const {queryByPlaceholderText} = render(<LoginPage/>);
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
            renderLoginPage({actions});
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
            renderLoginPage({actions});
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
            const {queryByText, findByText} = renderLoginPage({actions});
            // when
            fireEvent.click(button);
            // then
            await waitFor(() => findByText('Login failed'));
            const message = queryByText('Login failed');
            expect(message).toBeInTheDocument();
        });


        it('block the button when there is an ongoing api call', () => {
            // given
            const actions = {
                postLogin: mockAsyncDelayed()
            }
            renderLoginPage({actions});
            // when
            fireEvent.click(button);
            fireEvent.click(button);
            // then
            expect(actions.postLogin).toHaveBeenCalledTimes(1);
        });

        it('displays progress indicator when there is ongoing api call', () => {
            // given
            const actions = {
                postLogin: mockAsyncDelayed()
            };
            const {queryByText} = renderLoginPage({actions});
            // when
            fireEvent.click(button);
            // then
            const spinner = queryByText('Loading...');
            expect(spinner).toBeInTheDocument();
        });

        it('hides progress indicator when after api calls finishes with success', async () => {
            // given
            const actions = {
                postLogin: mockAsyncDelayed()
            };
            const {queryByText} = renderLoginPage({actions});
            // when
            fireEvent.click(button);
            await waitForElementToBeRemoved(() => queryByText('Loading...'));
            // then
            const spinner = queryByText('Loading...');
            expect(spinner).not.toBeInTheDocument();
        });

        it('hides progress indicator when after api calls finishes with error', async () => {
            // given
            const actions = {
                postLogin: jest.fn().mockImplementation(() => {
                    return new Promise((resolve, reject) => {
                        setTimeout(() => reject({
                            response: {data: {}}
                        }), 300);
                    });
                })
            };
            const {queryByText} = renderLoginPage({actions});
            // when
            fireEvent.click(button);
            await waitForElementToBeRemoved(() => queryByText('Loading...'));
            // then
            const spinner = queryByText('Loading...');
            expect(spinner).not.toBeInTheDocument();
        });


        it('does not throw error when actions not provided in props', () => {
            // given
            const {container, queryByPlaceholderText} = renderLoginPage();
            // when
            const clicking = () => fireEvent.click(button);
            // then
            expect(clicking).not.toThrow();
        });

        it('enables the button when username and password is not empty', () => {
            // given
            renderLoginPage();
            // when
            fireEvent.change(usernameInput, changeEvent('notEmptyUsername'));
            fireEvent.change(passwordInput, changeEvent('notEmptyPassword123'));
            // then
            expect(button).not.toBeDisabled();
        });

        it('disables the button when username is empty', () => {
            // given
            renderLoginPage();
            // when
            fireEvent.change(usernameInput, changeEvent(''));
            fireEvent.change(passwordInput, changeEvent('notEmptyPassword123'));
            // then
            expect(button).toBeDisabled();
        });

        it('disables the button when password is empty', () => {
            // given
            renderLoginPage();
            // when
            fireEvent.change(usernameInput, changeEvent('notEmptyUsername'));
            fireEvent.change(passwordInput, changeEvent(''));
            // then
            expect(button).toBeDisabled();
        });

        it('redirects to home page after successful login', async () => {
            // given
            const actions = {
                postLogin: jest.fn().mockResolvedValueOnce({})
            };
            const history = {
                push: jest.fn()
            }
            const {queryByText} = renderLoginPage({actions, history});
            // when
            fireEvent.click(button);
            await waitForElementToBeRemoved(() => queryByText('Loading...'));
            // then
            expect(history.push).toHaveBeenCalledWith('/');
        });
    });
});

console.error = () => {
}