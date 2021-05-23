import React, {Component} from 'react';
import {Alert, Card, Spinner} from 'react-bootstrap';
import ErrorAlert from "../components/ErrorAlert";
import * as apiCalls from '../api/apiCalls';
import userNotFoundImage from '../assets/user-not-found.jpeg';
import NoticeboardItem from "../components/NoticeboardItem";
import UserPageHeader from "../components/profile-page/UserPageHeader";
import {connect} from "react-redux";
import PaginationBar from "../components/PaginationBar";


class UserProfilePage extends Component {
    _isMounted = false;

    state = {
        errorMessage: false,
        isLoading: false,
        user: undefined,
        open: false,
        ongoingApiCall: false,
        errors: [],
        selectedImage: undefined,
        page: {
            content: [],
            number: 0,
            size: 18,
            totalElements: 0
        },
        currentPage: 0
    }

    componentWillUnmount() {
        this._isMounted = false;
    }

    componentDidMount() {
        this._isMounted = true;
        const username = this.props.match.params.username;
        if (!username) {
            return;
        }
        this.loadUserToState(username);
        this.loadUserNotices(username);
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        this._isMounted = true;
        if (this.props.match.params.username !== prevProps.match.params.username) {
            const username = this.props.match.params.username;
            if (!username) {
                return;
            }
            this.loadUserToState(username);
            this.loadUserNotices(username);
        }
    }

    onClickUpdateUser = (id, body) => {
        this.setState({ongoingApiCall: true});
        if (this.state.selectedImage) {
            body.profileImage = this.state.selectedImage.split(',')[1];
        }
        return apiCalls.updateUser(id, body)
            .then(response => {
                this.setState({
                    user: response.data,
                    open: false,
                    ongoingApiCall: false,
                    errors: [],
                    selectedImage: undefined
                });
            })
            .catch(apiError => {
                let errors = {...this.state.errors};
                if (apiError.response.data && apiError.response.data.validationErrors) {
                    errors = {...apiError.response.data.validationErrors}
                }
                this.setState({ongoingApiCall: false, errors});
                if (errors.profileImage) {
                    this.setState({selectedImage: undefined});
                }
            });
    }

    onClickCollapseButton = () => {
        if (this.state.open === true) {
            this.setState({selectedImage: undefined, open: false, errors: []});
        } else {
            this.setState({open: !this.state.open});
        }
    }

    loadUserToState(username) {
        if (this._isMounted) {
            this.setState({errorMessage: false, isLoading: true});
        }
        apiCalls.getUser(username)
            .then(response => {
                if (this._isMounted) {
                    this.setState({user: response.data, isLoading: false});
                }
            })
            .catch(error => {
                if (this._isMounted) {
                    if (error.response.status === 404) {
                        this.setState({errorMessage: true, isLoading: false})
                    } else {
                        this.setState({connectionError: true, isLoading: false})
                    }
                }
            });
    }

    loadUserNotices(username, page = 0) {
        apiCalls.getNoticesByUsername(username, page)
            .then(response => {
                if (this._isMounted) {
                    this.setState({page: response.data});
                }
            })
            .catch(error => {

            });
    }

    onClickNext = () => {
        if (!this.state.page.last) {
            this.setState({currentPage: this.state.currentPage + 1}, () => {
                const username = this.props.match.params.username;
                if (!username) {
                    return;
                }
                this.loadUserNotices(username, this.state.currentPage);
            });
        }
    }

    onClickPrevious = () => {
        if (!this.state.page.first) {
            this.setState({currentPage: this.state.currentPage - 1}, () => {
                const username = this.props.match.params.username;
                if (!username) {
                    return;
                }
                this.loadUserNotices(username, this.state.currentPage);
            });
        }
    }

    onClickFirst = () => {
        this.setState({currentPage: 0}, () => {
            const username = this.props.match.params.username;
            if (!username) {
                return;
            }
            this.loadUserNotices(username, this.state.currentPage);
        });
    }

    onClickLast = () => {
        this.setState({currentPage: this.state.page.totalPages - 1}, () => {
            const username = this.props.match.params.username;
            if (!username) {
                return;
            }
            this.loadUserNotices(username, this.state.currentPage);
        });
    }

    blobToFile(theBlob, fileName) {
        theBlob.lastModifiedDate = new Date();
        theBlob.name = fileName;
        return theBlob;
    }

    crop(url, aspectRatio) {
        return new Promise((resolve) => {
            const inputImage = new Image();

            inputImage.onload = () => {
                const inputWidth = inputImage.naturalWidth;
                const inputHeight = inputImage.naturalHeight;

                const inputImageAspectRatio = inputWidth / inputHeight;

                let outputWidth = inputWidth;
                let outputHeight = inputHeight;
                if (inputImageAspectRatio > aspectRatio) {
                    outputWidth = inputHeight * aspectRatio;
                } else if (inputImageAspectRatio < aspectRatio) {
                    outputHeight = inputWidth / aspectRatio;
                }

                const outputX = (outputWidth - inputWidth) * 0.5;
                const outputY = (outputHeight - inputHeight) * 0.5;

                const outputImage = document.createElement("canvas");

                outputImage.width = outputWidth;
                outputImage.height = outputHeight;

                const ctx = outputImage.getContext("2d");
                ctx.drawImage(inputImage, outputX, outputY);
                resolve(this.blobToFile(outputImage, 'test'));
            };

            inputImage.src = url;
        });
    }


    onImageSelect = event => {
        delete this.state.errors.profileImage;
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    // if (this.props.errors) {
                    //     delete this.props.errors.primaryImage;
                    // }
                    this.crop(fileReader.result, 1).then(croppedImage => {
                        // this.setState({primaryImage: croppedImage.toDataURL(), primaryImageError: undefined});
                        this.setState({selectedImage: croppedImage.toDataURL()});
                    });
                }
                fileReader.readAsDataURL(file);
            } else {
                let errors = {...this.state.errors};
                this.setState({
                    errors: {
                        ...errors,
                        profileImage: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                    }
                });
            }

        }
    }

    displaySpinner() {
        return <div className="text-center mt-5">
            <Spinner animation="border" size="sm" role="status" className="ms-1 mt-5">
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
            <Card style={{marginBottom: "90px"}}>
                <UserPageHeader
                    open={this.state.open}
                    user={this.state.user}
                    canBeModified={canBeModified}
                    onClickUpdateUser={this.onClickUpdateUser}
                    onClickCollapseButton={this.onClickCollapseButton}
                    ongoingApiCall={this.state.ongoingApiCall}
                    errors={this.state.errors}
                    selectedImage={this.state.selectedImage}
                    onImageSelect={this.onImageSelect}
                />
                <div className="row m-3 mt-2">
                    {!this.state.page.totalElements &&
                    <div className="text-center text-muted mt-2 row align-items-center mh-profile-card">
                        <div className="col">Ta tablica jest jeszcze pusta</div>
                    </div>
                    }
                    {this.state.page.totalElements > 0 && this.state.page.content.map(notice =>
                        <NoticeboardItem
                            title={notice.title}
                            price={notice.price}
                            location={notice.location}
                            image={notice.primaryImage}
                            createdAt={notice.createdAt}
                            id={notice.id}
                            key={notice.id}
                        />
                    )
                    }
                    {!this.state.isLoadingContent && (this.state.page.totalPages > 1) &&
                    <PaginationBar
                        page={this.state.page}
                        onClickFirst={this.onClickFirst}
                        onClickLast={this.onClickLast}
                        onClickPrevious={this.onClickPrevious}
                        onClickNext={this.onClickNext}
                    />
                    }
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