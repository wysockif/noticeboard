import React, {Component} from 'react';
import * as apiCalls from '../api/apiCalls';
import {Spinner} from "react-bootstrap";
import {Link} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {connect} from "react-redux";


class VerificationEmailAddressPage extends Component {

    state = {
        timeInSeconds: 120,
        isLoading: false,
        errorMessage: '',
        verified: undefined,
        isLinkDisabled: true
    }
    interval;

    componentDidMount() {
        if (this.props.isLoggedIn) {
            this.props.history.replace('/');
        }
        this.interval = setInterval(() => {
            if (this.state.timeInSeconds) {
                this.setState({timeInSeconds: this.state.timeInSeconds - 1});
            }
        }, 1000);

        const token = this.props.match.params.token;

        if (token) {
            this.setState({isLoading: true, errorMessage: '', verified: false})
            apiCalls.verifyToken(token)
                .then(response => {
                    this.setState({isLoading: false, verified: true});
                })
                .catch(apiError => {
                    this.setState({isLoading: true, errorMessage: apiError.response.data.message})
                })
        }
    }

    displayInfoAboutSentEmail = () => {
        const email = this.props.location.state.user ? this.props.location.state.user.email : this.props.location.state.email;
        const name = this.props.location.state.user ? ` ${this.props.location.state.user.firstName.trim()}` : '';
        return (
            <div className="text-center mt-5">
                <div>
                    {this.props.location.state.user.firstName && <h2>{`Dziękujemy za rejestrację${name},`}</h2>}
                    <div className="col-6 mx-auto">
                        na Twój adres e-mail ({email})
                        została wysłana wiadomość z linkiem umożliwiającym aktywację konta.
                    </div>
                    <div className="col-7 mx-auto mt-3 text-secondary">
                        Ważność tego linku wygaśnie w ciągu 15 minut od jego generacji.
                    </div>
                    <Link to="/login"
                          className="btn btn-outline-light my-4 px-4"
                          style={{backgroundColor: '#b78e56'}}
                          variant="outline-light"
                    >
                        Przejdź do strony logowania
                    </Link>
                    <div className="col-7 mx-auto text-muted">
                        <p><small>Jeżeli nie widzisz tego e-maila sprawdź folder ze spamem.</small></p>
                        <small>E-mail nie dotarł? </small>
                        {this.displayTryAgain()}
                    </div>
                </div>
            </div>
        );
    }

    displayTryAgain() {
        return <>
            {this.state.timeInSeconds > 0 && <small>Spróbuj ponownie za {this.state.timeInSeconds}s.</small>}
            {!this.state.timeInSeconds && <Link to={{
                pathname: '/activate',
                state: {
                    email: this.props.location.state.user.email
                }
            }} className="text-muted">
                <small>Spróbuj ponownie</small>
            </Link>}
        </>;
    }

    displayLoadingInfo() {
        return <div className="text-center mt-5 text-muted">
            Trwa weryfikacja
            <Spinner animation="border" size="sm" role="status" className="mt-1 ms-1">
                <span className="sr-only">Loading...</span>
            </Spinner>
        </div>;
    }

    displayErrorMessage() {
        return <div className="text-center mt-5">
            <div className="col-7 mx-auto">
                {this.state.errorMessage}
            </div>
            <Link to={{
                pathname: '/activate'
            }} className="text-muted"><small>Spróbuj ponownie</small></Link>
        </div>;
    }

    displaySuccessMessage() {
        return <div className="text-center mt-5">
            <h1><FontAwesomeIcon icon={["far", "check-circle"]}/></h1>
            <h5>Weryfikacja przebiegła pomyślnie.</h5>
            <Link to="/login"
                  className="btn btn-outline-light my-3 px-4"
                  style={{backgroundColor: '#b78e56'}}
                  variant="outline-light"
            >
                Przejdź do strony logowania
            </Link>
        </div>;
    }

    render() {
        if (this.state.timeInSeconds === 0) {
            clearInterval(this.interval);
        }
        let content;
        if (this.state.errorMessage) {
            content = this.displayErrorMessage();
        } else if (this.state.verified) {
            content = this.displaySuccessMessage();
        } else if (this.state.isLoading) {
            content = this.displayLoadingInfo();
        } else if (this.props.location.state &&
            (this.props.location.state.user || this.props.location.state.email)) {
            content = this.displayInfoAboutSentEmail();
        } else if (!this.props.match.params.token) {
            this.props.history.replace('/');
        }
        return (
            <div>
                {content}
            </div>
        );
    }
}

VerificationEmailAddressPage.defaultProps = {
    match: {
        params: {}
    },
    location: {
        state: {}
    }
};

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn
    };
};

export default connect(mapStateToProps)(VerificationEmailAddressPage);
