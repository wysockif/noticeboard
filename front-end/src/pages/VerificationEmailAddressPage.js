import React, {Component} from 'react';
import * as apiCalls from '../api/apiCalls';
import {Spinner} from "react-bootstrap";
import {Redirect} from "react-router";
import {Link} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";


class VerificationEmailAddressPage extends Component {

    state = {
        isLoading: false,
        errorMessage: '',
        verified: undefined
    }

    componentDidMount() {
        const token = this.props.match.params.token;
        if (token) {
            this.setState({isLoading: true, errorMessage: '', verified: false})
            apiCalls.verifyToken(token)
                .then(response => {
                    this.setState({isLoading: false, verified: true});
                })
                .catch(apiError => {
                    this.setState({isLoading: false, errorMessage: apiError.response.data.message})
                })
        }
    }

    displayInfoAboutSentEmail = () => {
        return (
            <div className="text-center mt-5">
                <div>
                    <h1>{`Witaj ${this.props.location.state.user.firstName},`}</h1>
                    <div>na Twój adres email ({this.props.location.state.user.email}) został wysłany link
                        aktywacyjny do Twojego konta.
                    </div>
                    <Link to="/login"
                          className="btn btn-outline-light my-4 px-4"
                          style={{backgroundColor: '#b78e56'}}
                          variant="outline-light"
                    >
                        Przejdź do strony logowania
                    </Link>
                </div>
            </div>
        );
    }

    displayLoadingInfo() {
        return <div className="text-center mt-5">
            <div><h3>Trwa weryfikacja</h3></div>
            <Spinner animation="border" size="sm" role="status" className="mt-2">
                <span className="sr-only">Loading...</span>
            </Spinner>
        </div>;
    }

    displayErrorMessage() {
        return <div className="text-center mt-5">
            <h5>{this.state.errorMessage}</h5>
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

    redirectToLogin() {
        return <div className="text-center mt-5">
            <Redirect to="/login"/>
        </div>
    };

    render() {
        let content;
        if (this.state.errorMessage) {
            content = this.displayErrorMessage();
        } else if (this.state.verified) {
            content = this.displaySuccessMessage();
        } else if (this.state.isLoading) {
            content = this.displayLoadingInfo();
        } else if (this.props.location.state && this.props.location.state.user) {
            content = this.displayInfoAboutSentEmail();
        } else if (!this.props.match.params.token) {
            content = this.redirectToLogin();
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
    }
}

export default VerificationEmailAddressPage;
