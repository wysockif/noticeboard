import React, {Component} from 'react';
import {Alert, Card, Spinner} from 'react-bootstrap';
import ErrorAlert from "../components/ErrorAlert";
import * as apiCalls from '../api/apiCalls';
import userNotFoundImage from '../assets/user-not-found.jpeg';
import defaultProfilePicture from '../assets/default-profile-image.jpeg';
import NoticeboardItem from "../components/NoticeboardItem";
import UserProfilePageHeader from "../components/UserProfilePageHeader";


class UserProfilePage extends Component {
    state = {
        errorMessage: false,
        isLoading: false,
        user: undefined
    }

    componentDidMount() {
        this.loadUserToState();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (this.props.match.params.username !== prevProps.match.params.username) {
            this.loadUserToState();
        }
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
            <Alert className="col-5 mx-auto" variant="danger">Nie można załadować strony. Spróbuj ponownie
                później.</Alert>
        </div>);
    }

    displayMainContent() {
        let profileImage = defaultProfilePicture;
        if (this.state.user && this.state.user.image) {
            profileImage = this.state.user.image;
        }
        return <div data-testid="homepage">
            <Card>
                <UserProfilePageHeader profileImage={profileImage} user={this.state.user}/>
                <div className="row m-4">
                    <NoticeboardItem title="Sprzedam Opla" price="3000 zł" location="Warszawa" id="12"/>
                    <NoticeboardItem title="Komputer" price="2200 zł" location="Kraków" id="14"/>
                    <NoticeboardItem title="Zabawki dla psa" price="140" location="Warszawa" id={"15"}/>
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

export default UserProfilePage;