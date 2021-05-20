import React, {Component} from 'react';
import {Container} from 'react-bootstrap';
import {Redirect} from 'react-router';
import ButtonWithSpinner from '../components/ButtonWithSpinner';
import InputWithValidation from '../components/InputWithValidation';
import {connect} from 'react-redux';

export class RegistrationPage extends Component {
    state = {
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        password: '',
        passwordRepeat: '',
        ongoingApiCall: false,
        errors: [],
        isPasswordRepeatCorrect: true,
    }

    onChangeFirstName = event => {
        const errors = {...this.state.errors};
        delete errors.firstName;
        this.setState({firstName: event.target.value, errors});
    }

    onChangeLastName = event => {
        const errors = {...this.state.errors};
        delete errors.lastName;
        this.setState({lastName: event.target.value, errors});
    }

    onChangeUsername = event => {
        if (this.state.username !== event.target.value.trim()) {
            const errors = {...this.state.errors};
            delete errors.username;
            this.setState({username: event.target.value.trim(), errors});
        }
    }

    onChangeEmail = event => {
        if (this.state.email !== event.target.value.trim()) {
            const errors = {...this.state.errors};
            delete errors.email;
            this.setState({email: event.target.value.trim(), errors});
        }
    }

    onChangePassword = event => {
        if (this.state.password !== event.target.value.trim()) {
            const errors = {...this.state.errors};
            delete errors.password;
            const isPasswordRepeatCorrect = this.state.passwordRepeat === event.target.value ? true : false;
            this.setState({password: event.target.value, isPasswordRepeatCorrect, errors});
        }
    }

    onChangePasswordRepeat = event => {
        if (this.state.passwordRepeat !== event.target.value.trim()) {
            const errors = {...this.state.errors};
            delete errors.passwordRepeat;
            const isPasswordRepeatCorrect = this.state.password === event.target.value;
            this.setState({passwordRepeat: event.target.value, isPasswordRepeatCorrect, errors});
        }
    }

    onClickRegister = () => {
        const user = {
            firstName: this.state.firstName.trim(),
            lastName: this.state.lastName.trim(),
            username: this.state.username.trim(),
            email: this.state.email.trim(),
            password: this.state.password.trim()
        }
        this.setState({ongoingApiCall: true});
        this.props.actions.postRegister(user)
            .then(response => {
                this.setState({ongoingApiCall: false}, () => {
                    this.props.history.push({
                        pathname: '/verify',
                        state: {
                            user
                        }
                    });
                });

            })
            .catch(apiError => {
                let errors = {...this.state.errors};
                if (apiError.response.data && apiError.response.data.validationErrors) {
                    errors = {...apiError.response.data.validationErrors}
                }
                this.setState({ongoingApiCall: false, errors});
            });
    }

    everyFieldHasBeenCompleted = () => {
        const {firstName, lastName, username, email, password, passwordRepeat} = this.state;
        if (firstName !== '' && lastName !== '' && username !== ''
            && email !== '' & password !== '' && passwordRepeat !== '') {
            return true;
        } else {
            return false;
        }
    }

    render() {
        return (
            <Container className="col-11 col-sm-10 col-md-9 col-lg-7 col-xl-5 mt-5">
                {this.props.isLoggedIn && <Redirect to="/"/>}
                <h1 className="text-center my-4">Zarejestruj się</h1>

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
                <div className="mb-3 mt-4 text-center">
                    <ButtonWithSpinner
                        onClick={this.onClickRegister}
                        disabled={
                            !this.everyFieldHasBeenCompleted() ||
                            this.state.ongoingApiCall ||
                            !this.state.isPasswordRepeatCorrect}
                        content="Zarejestruj się"
                        ongoingApiCall={this.state.ongoingApiCall}
                    />
                </div>
            </Container>
        );
    }
}

RegistrationPage.defaultProps = {
    actions: {
        postRegister: () =>
            new Promise((resolve, reject) => {
                resolve({});
            })
    },
    history: {
        push: () => {
        }
    }
};

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn
    }
}


export default connect(mapStateToProps)(RegistrationPage);