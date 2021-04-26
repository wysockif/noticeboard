import React, {Component} from 'react';
import {Alert, Card, Spinner} from 'react-bootstrap';
import ErrorAlert from "../components/ErrorAlert";
import * as apiCalls from '../api/apiCalls';
import userNotFoundImage from '../assets/user-not-found.jpeg';
import NoticeboardItem from "../components/NoticeboardItem";
import UserProfilePageHeader from "../components/profile-page/UserProfilePageHeader";
import {connect} from "react-redux";


class UserProfilePage extends Component {
    state = {
        errorMessage: false,
        isLoading: false,
        user: undefined,
        open: false,
        ongoingApiCall: false,
        errors: []
    }

    componentDidMount() {
        this.loadUserToState();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (this.props.match.params.username !== prevProps.match.params.username) {
            this.loadUserToState();
        }
    }

    onClickUpdateUser = (id, body) => {
        this.setState({ongoingApiCall: true});
        return apiCalls.updateUser(id, body)
            .then(response => {
                this.setState({user: response.data, open: false, ongoingApiCall: false, errors: []});
            })
            .catch(apiError => {
                let errors = { ...this.state.errors };
                if (apiError.response.data && apiError.response.data.validationErrors) {
                    errors = { ...apiError.response.data.validationErrors }
                }
                this.setState({ ongoingApiCall: false, errors });
            });
    }

    loadUserToState() {
        const username = this.props.match.params.username;
        if (!username) {
            return;
        }
        this.setState({errorMessage: false, isLoading: true});
        apiCalls.getUser(username)
            .then(response => {
                this.setState({user: response.data, isLoading: false});
            })
            .catch(error => {
                if (error.response.status === 404) {
                    this.setState({errorMessage: true, isLoading: false})
                } else {
                    this.setState({connectionError: true, isLoading: false})
                }
            });
    }

    displaySpinner() {
        return <div className="text-center">
            <Spinner animation="border" size="sm" role="status" className="ms-1">
                <span className="sr-only">Loading...</span>
            </Spinner>
        </div>;
    }

    displayUserNotFoundAlert() {
        return (
            <ErrorAlert image={userNotFoundImage}/>
        );
    }

    displayConnectionError() {
        return (<div className="text-center">
            <Alert className="col-5 mx-auto" variant="danger">
                Nie można załadować strony. Spróbuj ponownie później.
            </Alert>
        </div>);
    }


    displayMainContent() {
        const canBeModified = this.props.match.params.username === this.props.loggedInUser.username;
        return <div data-testid="homepage">
            <Card>
                <UserProfilePageHeader
                    open={this.state.open}
                    user={this.state.user}
                    canBeModified={canBeModified}
                    onClickUpdateUser={this.onClickUpdateUser}
                    onClickCollapseButton={() => this.setState({open: !this.state.open})}
                    ongoingApiCall={this.state.ongoingApiCall}
                    errors={this.state.errors}
                />
                <div className="row m-4">
                    <NoticeboardItem title="Sprzedam Opla" price="3000 zł" location="Warszawa" id="12"/>
                    <NoticeboardItem title="Komputer" price="2200 zł" location="Kraków" id="14"/>
                    <NoticeboardItem title="Zabawki dla psa" price="140 zł" location="Warszawa" id={"15"}/>
                    {/*<NoticeboardItem title="Zabawki dla kota" price="140 zł" location="Warszawa" id={"16"}/>*/}
                    {/*<NoticeboardItem title="Sofa" price="140 zł" location="Warszawa" id={"17"}/>*/}
                    {/*<NoticeboardItem title="Kaloryfer" price="140 zł" location="Warszawa" id={"18"}/>*/}
                    {/*<NoticeboardItem title="Monitor" price="140 zł" location="Warszawa" id={"19"}/>*/}
                    <NoticeboardItem title="Poduszka" price="140 zł" location="Warszawa" id={"20"}/>
                </div>
            </Card>
        </div>;
    }

    render() {
        let content;

        if (this.state.isLoading) {
            content = this.displaySpinner();
        } else if (this.state.errorMessage) {
            content = this.displayUserNotFoundAlert();
        } else if (this.state.connectionError) {
            content = this.displayConnectionError();
        } else {
            content = this.displayMainContent();
        }

        return (
            <div data-testid="userprofilepage">
                {content}
            </div>
        );
    }
}

UserProfilePage.defaultProps = {
    match: {
        params: {}
    }
};

const mapStateToProps = state => {
    return {
        loggedInUser: state
    };
};

export default connect(mapStateToProps)(UserProfilePage);