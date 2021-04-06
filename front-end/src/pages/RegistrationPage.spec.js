import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { RegistrationPage } from './RegistrationPage';

describe('RegistrationPage', () => {
    describe('Layout', () => {
        it('has registration header', () => {
            // given
            const { container } = render(<RegistrationPage />);
            // when
            const header = container.querySelector('h1');
            // then
            expect(header).toHaveTextContent('Rejestracja');
        });

        it('has input for first name', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const firstNameInput = queryByPlaceholderText('Imię');
            // then
            expect(firstNameInput).toBeInTheDocument();
        });

        it('has input for last name', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const lastNameInput = queryByPlaceholderText('Nazwisko');
            // then
            expect(lastNameInput).toBeInTheDocument();
        });

        it('has input for username', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            // then
            expect(usernameInput).toBeInTheDocument();
        });

        it('has input for email', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const usernameInput = queryByPlaceholderText('Adres email');
            // then
            expect(usernameInput).toBeInTheDocument();
        });

        it('has input for password', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const passwordInput = queryByPlaceholderText('Hasło');
            // then
            expect(passwordInput).toBeInTheDocument();
        });

        it('has password type for input for password', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const passwordInput = queryByPlaceholderText('Hasło');
            // then
            expect(passwordInput.type).toBe('password');
        });

        it('has input for password repeat', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const passwordRepeatInput = queryByPlaceholderText('Powtórz hasło');
            // then
            expect(passwordRepeatInput).toBeInTheDocument();
        });

        it('has password type for input for password repeat', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            // when
            const passwordRepeatInput = queryByPlaceholderText('Powtórz hasło');
            // then
            expect(passwordRepeatInput.type).toBe('password');
        });

        it('has submit button', () => {
            // given
            const { container } = render(<RegistrationPage />);
            // when
            const button = container.querySelector('button');
            // then
            expect(button).toBeInTheDocument();
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
        let firstNameInput, lastNameInput, usernameInput, emailInput, passwordInput, passwordRepeatInput, button;

        const setupForSubmit = props => {
            const rendered = render(<RegistrationPage {...props} />);
            const { container, queryByPlaceholderText } = rendered;
            firstNameInput = queryByPlaceholderText('Imię');
            lastNameInput = queryByPlaceholderText('Nazwisko');
            usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            emailInput = queryByPlaceholderText('Adres email');
            passwordInput = queryByPlaceholderText('Hasło');
            passwordRepeatInput = queryByPlaceholderText('Powtórz hasło');
            fireEvent.change(firstNameInput, changeEvent('Updatedfirstname'));
            fireEvent.change(lastNameInput, changeEvent('Updatedlastname'));
            fireEvent.change(usernameInput, changeEvent('updatedusername'));
            fireEvent.change(emailInput, changeEvent('updatedemail@mail.com'));
            fireEvent.change(passwordInput, changeEvent('Password123'));
            fireEvent.change(passwordRepeatInput, changeEvent('Password123'));
            button = container.querySelector('button');
            return rendered;
        };



        it('sets the first name value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            const firstNameInput = queryByPlaceholderText('Imię');
            // when
            fireEvent.change(firstNameInput, changeEvent('Updatedfirstname'));
            // then
            expect(firstNameInput).toHaveValue('Updatedfirstname');
        });

        it('sets the last name value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            const lastNameInput = queryByPlaceholderText('Nazwisko');
            // when
            fireEvent.change(lastNameInput, changeEvent('Updatedlastname'));
            // then
            expect(lastNameInput).toHaveValue('Updatedlastname');
        });

        it('sets the username value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            const usernameInput = queryByPlaceholderText('Nazwa użytkownika');
            // when
            fireEvent.change(usernameInput, changeEvent('updatedusername'));
            // then
            expect(usernameInput).toHaveValue('updatedusername');
        });

        it('sets the email value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            const emailInput = queryByPlaceholderText('Adres email');
            // when
            fireEvent.change(emailInput, changeEvent('updatedemail@mail.com'));
            // then
            expect(emailInput).toHaveValue('updatedemail@mail.com');
        });

        it('sets the password value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            const passwordInput = queryByPlaceholderText('Hasło');
            // when
            fireEvent.change(passwordInput, changeEvent('NoweHaslo123'));
            // then
            expect(passwordInput).toHaveValue('NoweHaslo123');
        });

        it('sets the password repeat value into state', () => {
            // given
            const { queryByPlaceholderText } = render(<RegistrationPage />);
            const passwordRepeatInput = queryByPlaceholderText('Powtórz hasło');
            // when
            fireEvent.change(passwordRepeatInput, changeEvent('NoweHaslo123'));
            // then
            expect(passwordRepeatInput).toHaveValue('NoweHaslo123');
        });

        it('calls postRegister when the fields are valid and the actions are provided in props', () => {
            // given
            const actions = {
                postRegister: jest.fn().mockResolvedValueOnce({})
            };
            setupForSubmit({ actions });
            // when
            fireEvent.click(button);
            // then
            expect(actions.postRegister).toHaveBeenCalledTimes(1);
        });

        it('does not throw exception when actions not provided in props', () => {
            // given
            setupForSubmit();
            // when
            const doesThrow = fireEvent.click(button);
            // then
            expect(() => doesThrow).not.toThrow();
        });

        it('calls postRegister with user body when the fields are valid', () => {
            // given
            const actions = {
                postRegister: jest.fn().mockResolvedValueOnce({})
            };
            setupForSubmit({ actions });
            // when
            fireEvent.click(button);
            // then
            const expectedUser = {
                firstName: 'Updatedfirstname',
                lastName: 'Updatedlastname',
                username: 'updatedusername',
                email: 'updatedemail@mail.com',
                password: 'Password123'
            }
            expect(actions.postRegister).toHaveBeenCalledWith(expectedUser);
        });
    });
});

console.error = () => {}