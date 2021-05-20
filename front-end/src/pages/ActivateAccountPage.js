import React, {Component} from 'react';
import InputWithValidation from "../components/InputWithValidation";
import {Container} from "react-bootstrap";
import {Redirect} from "react-router";
import ButtonWithSpinner from "../components/ButtonWithSpinner";
import * as apiCalls from "../api/apiCalls";
import {connect} from "react-redux";

class ActivateAccountPage extends Component {

    state = {
        firstName: '',
        email: '',
        ongoingApiCall: false,
        emailError: undefined
    }

    componentDidMount() {
        if (this.props.isLoggedIn) {
            this.props.history.replace('/');
        }
        if (this.props.location.state && this.props.location.state.email) {
            this.setState({email: this.props.location.state.email})
        }
        if (this.props.location.state && this.props.location.state.username) {
            apiCalls.getUser(this.props.location.state.username)
                .then(response => {
                    this.setState({email: response.data.email, isLoading: false});
                });
        }
    }

    onChangeEmail = event => {
        this.setState({emailError: undefined, email: event.target.value});
    }

    onClickSendLink = () => {
        this.setState({ongoingApiCall: true})
        apiCalls.activateAccount(this.state.email)
            .then(response => {
                this.setState({ongoingApiCall: false});
                this.props.history.replace({
                    pathname: '/verify',
                    state: {
                        user: response.data
                    }
                });
            })
            .catch(apiError => {
                this.setState({ongoingApiCall: false, emailError: apiError.response.data.message});
            });
    }

    render() {
        return (
            <Container className="col-11 col-sm-10 col-md-9 col-lg-7 col-xl-5 mt-5">
                {this.props.isLoggedIn && <Redirect to="/"/>}
                <h1 className="text-center my-4">Aktywuj konto</h1>
                <InputWithValidation
                    label="Adres email:" placeholder="Adres email" icon="at"
                    value={this.state.email}
                    onChange={this.onChangeEmail}
                    hasError={this.state.emailError !== undefined}
                    error={this.state.emailError}
                />
                <div className="mb-3 mt-4 text-center">
                    <ButtonWithSpinner
                        onClick={this.onClickSendLink}
                        disabled={!this.state.email.match(/^[^\s@]+@[^\s@]+$/)}
                        content="Wyślij link aktywacyjny"
                        ongoingApiCall={this.state.ongoingApiCall}
                    />
                </div>
            </Container>
        );
    };
}

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn
    };
}

export default connect(mapStateToProps)(ActivateAccountPage);
