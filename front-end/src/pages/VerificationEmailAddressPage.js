import React, {Component} from 'react';
import * as apiCalls from '../api/apiCalls';
import {Spinner} from "react-bootstrap";


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
                {this.props.location.state && this.props.location.state.user &&
                <div>
                    <h1>{`Cześć ${this.props.location.state.user.firstName},`}</h1>
                    <div>na Twój adres email ({this.props.location.state.user.email}) został wysłany mail z linkiem
                        aktywacyjnym.
                    </div>
                </div>
                }
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
            <h5>Wystąpił błąd:</h5>
            <div>{this.state.errorMessage}</div>
        </div>;
    }

    displaySuccessMessage() {
        return <div className="text-center mt-5">
            <h6>Weryfikacja przebiegła pomyślnie. Możesz się zalogować.</h6>
        </div>;
    }

    render() {
        let content;
        if (this.state.errorMessage) {
            content = this.displayErrorMessage();
        } else if (this.state.verified) {
            content = this.displaySuccessMessage();
        } else if (this.state.isLoading) {
            content = this.displayLoadingInfo();
        } else if (this.state.verified) {
            content = this.displaySuccessMessage();
        } else {
            content = this.displayInfoAboutSentEmail();
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
