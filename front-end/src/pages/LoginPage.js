import React, {Component} from 'react';
import {connect} from 'react-redux';
import InputWithValidation from '../components/InputWithValidation';
import {Redirect} from 'react-router';
import {Alert, Container} from 'react-bootstrap';
import ButtonWithSpinner from '../components/ButtonWithSpinner';

export class LoginPage extends Component {
    state = {
        username: '',
        password: '',
        alertMessage: '',
        ongoingApiCall: false
    }

    onClickLoginButton = () => {
        const body = {
            username: this.state.username,
            password: this.state.password
        }
        this.setState({ongoingApiCall: true});
        this.props.actions.postLogin(body)
            .then((response) => {
                this.setState({ongoingApiCall: false}, () => {
                    this.props.history.push('/');
                    this.props.dispatch({
                        type: 'LOGIN_SUCCESS',
                        payload: {...response.data, isLoggedIn: true, password: body.password}
                    });
                });
            })
            .catch(error => {
                this.setState({ongoingApiCall: false})
                if (error.response) {
                    this.setState({alertMessage: error.response.data.message});
                }
            });
    }

    onChangeUsername = event => {
        this.setState({username: event.target.value, alertMessage: ''});
    }

    onChangePassword = event => {
        this.setState({password: event.target.value, alertMessage: ''});
    }

    onClickActivateAccount = () => {
        this.props.history.push({
            pathname: '/activate',
            state: {
                username: this.state.username
            }
        });
    }

    render() {
        const disableSubmit = !(this.state.username && this.state.password);

        return (
            <Container className="col-11 col-sm-10 col-md-9 col-lg-7 col-xl-5 mt-5">
                {this.props.isLoggedIn && <Redirect to="/"/>}
                <h1 className="text-center my-4">Zaloguj się</h1>
                <InputWithValidation
                    label="Nazwa użytkownika:" placeholder="Nazwa użytkownika" icon="at"
                    value={this.state.username}
                    onChange={this.onChangeUsername}
                />

                <InputWithValidation
                    label="Hasło:" placeholder="Hasło" type="password" icon="key"
                    value={this.state.password}
                    onChange={this.onChangePassword}
                />

                {this.state.alertMessage && <div>
                    <Alert variant="danger" className="text-center col-sm-10 col-md-8 mx-auto">
                        {this.state.alertMessage}
                    </Alert>
                    {this.state.alertMessage === 'To konto nie zostało aktywowane' &&
                    <div className="text-center col-sm-10 col-md-9 mx-auto">
                        <div className="btn btn-outline-light my-2 px-4 outline-light"
                             onClick={this.onClickActivateAccount}
                             style={{backgroundColor: '#b78e56'}}>
                            Aktywuj konto
                        </div>
                    </div>}
                </div>}

                <div className="mb-3 mt-4 text-center">
                    {this.state.alertMessage !== 'To konto nie zostało aktywowane' &&
                    <ButtonWithSpinner
                        onClick={this.onClickLoginButton}
                        disabled={disableSubmit || this.state.ongoingApiCall}
                        content="Zaloguj się"
                        ongoingApiCall={this.state.ongoingApiCall}
                    />
                    }
                </div>
            </Container>
        )
    }

}

LoginPage.defaultProps = {
    actions: {
        postLogin: () => new Promise((resolve, reject) => {
        })
    },
    dispatch: () => {
    }
};

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn
    };
}


export default connect(mapStateToProps)(LoginPage);