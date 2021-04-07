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
        pendingApiCall: false
    }

    onChangeFirstName = event => this.setState({ firstName: event.target.value });

    onChangeLastName = event => this.setState({ lastName: event.target.value });

    onChangeUsername = event => this.setState({ username: event.target.value });

    onChangeEmail = event => this.setState({ email: event.target.value });

    onChangePassword = event => this.setState({ password: event.target.value });

    onChangePasswordRepeat = event => this.setState({ passwordRepeat: event.target.value });

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
            .catch(error => {
                this.setState({ pendingApiCall: false });
            });
    }

    render() {
        return (
            <Container className="col-11 col-sm-9 col-md-7 col-lg-5 col-xl-4">
                <h1 className="text-center my-4">Rejestracja</h1>

                <InputWithValidation placeholder="Imię" icon="user" value={this.state.firstName} onChange={this.onChangeFirstName} />
                <InputWithValidation placeholder="Nazwisko" icon="user" value={this.state.lastName} onChange={this.onChangeLastName} />
                <InputWithValidation placeholder="Nazwa użytkownika" icon="at" value={this.state.username} onChange={this.onChangeUsername} />
                <InputWithValidation placeholder="Adres email" icon="at" value={this.state.email} onChange={this.onChangeEmail} />
                <InputWithValidation placeholder="Hasło" type="password" icon="key" value={this.state.password} onChange={this.onChangePassword} />
                <InputWithValidation placeholder="Powtórz hasło" type="password" icon="key" value={this.state.passwordRepeat} onChange={this.onChangePasswordRepeat} />
                <div className="mb-3 text-center" >
                    <Button style={{ backgroundColor: '#B84' }} variant="outline-light" onClick={this.onClickRegister} disabled={this.state.pendingApiCall}>
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