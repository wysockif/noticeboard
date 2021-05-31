import React, {Component} from 'react';
import ErrorAlert from "../components/ErrorAlert";
import pageNotFoundImage from '../assets/page-not-found.jpeg';


class ErrorPage extends Component {
    render() {
        return (
            <div data-testid="errorpage">
                <ErrorAlert image={pageNotFoundImage}/>
            </div>
        );
    }
}

export default ErrorPage;