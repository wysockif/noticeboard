import React, { Component } from 'react';
import { Container, Button, Spinner } from 'react-bootstrap';
import InputWithValidation from '../components/InputWithValidation';


export class RegistrationPage extends Component {
    state = {
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        password: '',
        passwordRepeat: '',
        pendingApiCall: false,
        errors: [],
        isPasswordRepeatCorrect: true
    }

    onChangeFirstName = event => {
        const errors = {...this.state.errors};
        delete errors.firstName;
        this.setState({ firstName: event.target.value, errors });
    }

    onChangeLastName = event => {
        const errors = {...this.state.errors};
        delete errors.lastName;
        this.setState({ lastName: event.target.value, errors});
    }

    onChangeUsername = event => {
        const errors = {...this.state.errors};
        delete errors.username;
        this.setState({ username: event.target.value, errors });
    }

    onChangeEmail = event => {
        const errors = {...this.state.errors};
        delete errors.email;
        this.setState({ email: event.target.value, errors });
    }

    onChangePassword = event => {
        const errors = {...this.state.errors};
        delete errors.password;
        const isPasswordRepeatCorrect = this.state.passwordRepeat === event.target.value ? true : false;
        this.setState({ password: event.target.value, isPasswordRepeatCorrect, errors });
    }

    onChangePasswordRepeat = event => {
        const errors = {...this.state.errors};
        delete errors.passwordRepeat;
        const isPasswordRepeatCorrect = this.state.password === event.target.value ? true : false;
        this.setState({ passwordRepeat: event.target.value, isPasswordRepeatCorrect, errors });
    }

    onClickRegister = () => {
        const user = {
            firstName: this.state.firstName,
            lastName: this.state.lastName,
            username: this.state.username,
            email: this.state.email,
            password: this.state.password
        }
        this.setState({ pendingApiCall: true });
        this.props.actions.postRegister(user)
            .then(response => {
                this.setState({ pendingApiCall: false });
            })
            .catch(apiError => {
                let errors = { ...this.state.errors };
                if (apiError.response.data && apiError.response.data.validationErrors) {
                    errors = { ...apiError.response.data.validationErrors }
                }
                this.setState({ pendingApiCall: false, errors });
            });
    }

    render() {
        return (
            <Container className="col-11 col-sm-9 col-md-7 col-lg-5 col-xl-4">
                <h1 className="text-center my-4">Rejestracja</h1>

                <InputWithValidation
                    label="Imię:" placeholder="Imię" icon="user"
                    value={this.state.firstName}
                    onChange={this.onChangeFirstName}
                    hasError={this.state.errors.firstName !== undefined}
                    error={this.state.errors.firstName}
                />
                <InputWithValidation
                    label="Nazwisko:" placeholder="Nazwisko" icon="user"
                    value={this.state.lastName}
                    onChange={this.onChangeLastName}
                    hasError={this.state.errors.lastName !== undefined}
                    error={this.state.errors.lastName}
                />
                <InputWithValidation
                    label="Nazwa użytkownika:" placeholder="Nazwa użytkownika" icon="at"
                    value={this.state.username}
                    onChange={this.onChangeUsername}
                    hasError={this.state.errors.username !== undefined}
                    error={this.state.errors.username}
                />
                <InputWithValidation
                    label="Adres email:" placeholder="Adres email" icon="at"
                    value={this.state.email}
                    onChange={this.onChangeEmail}
                    hasError={this.state.errors.email !== undefined}
                    error={this.state.errors.email}
                />
                <InputWithValidation
                    label="Hasło:" placeholder="Hasło" type="password" icon="key"
                    value={this.state.password}
                    onChange={this.onChangePassword}
                    hasError={this.state.errors.password !== undefined}
                    error={this.state.errors.password}
                />
                <InputWithValidation
                    label="Powtórz hasło:" placeholder="Powtórz hasło" type="password" icon="key"
                    value={this.state.passwordRepeat}
                    onChange={this.onChangePasswordRepeat}
                    hasError={!this.state.isPasswordRepeatCorrect}
                    isCorrect={this.state.isPasswordRepeatCorrect && this.state.passwordRepeat !== ''}
                    error="Hasła nie są identyczne"
                />
                <div className="mb-3 text-center" >
                    <Button style={{ backgroundColor: '#B84' }} variant="outline-light" onClick={this.onClickRegister}
                        disabled={this.state.pendingApiCall || !this.state.isPasswordRepeatCorrect}
                    >
                        Zarejestruj się
                        {this.state.pendingApiCall && <Spinner animation="border" size="sm" role="status" className="ms-1">
                            <span className="sr-only">Loading...</span>
                        </Spinner>}
                    </Button>
                </div>
            </Container >
        );
    }
}
RegistrationPage.defaultProps = {
    actions: {
        postRegister: () =>
            new Promise((resolve, reject) => {
                resolve({});
            })
    }
};

export default RegistrationPage;