import React, {Component} from 'react';
import {Card, Container, Spinner} from "react-bootstrap";
import BasicInformation from "../components/notice-form/BasicInformation";
import * as apiCalls from "../api/apiCalls";
import DescriptionForm from "../components/notice-form/DescriptionForm";
import ContactInformation from '../components/notice-form/ContactInformation'
import {connect} from "react-redux";

class EditNoticePage extends Component {

    state = {
        isLoading: true,
        title: '',
        location: '',
        price: '',
        description: '',
        ongoingApiCall: false,
        errors: undefined,
    }

    componentDidMount() {
        this.setState({isLoading: true})
        if (this.props.location && this.props.location.state && this.props.location.state.notice) {
            const {title, location, price, description} = this.props.location.state.notice;
            console.log(this.props.location.state.notice)
            const userId = this.props.location.state.userId;
            this.setState({title, location, price, description, userId, isLoading: false});
        } else {
            const noticeId = this.props.match.params.id;
            apiCalls.getNotice(noticeId)
                .then(response => {
                    const {title, location, price, description} = response.data;
                    this.setState({title, location, price, description, isLoading: false});
                })
                .catch(error => {
                    this.setState({isLoading: false});

                });
        }
    }

    onChangeTitle = event => {
        const errors = {...this.state.errors};
        delete errors.title;
        this.setState({title: event.target.value, errors});
    }

    onChangeLocation = event => {
        const errors = {...this.state.errors};
        delete errors.location;
        this.setState({location: event.target.value, errors});
    }

    onChangePrice = event => {
        const errors = {...this.state.errors};
        delete errors.price;
        this.setState({price: event.target.value, errors});
    }

    onChangeDescription = event => {
        const errors = {...this.state.errors};
        delete errors.description;
        const value = event.target.value;
        if (value.length > 2000) {
            errors.description = 'Opis ogłoszenia nie może być dłuższy niż 2000 znaków';
            this.setState({errors});
            return;
        }
        this.setState({description: event.target.value, errors});
    }

    returnMainContent = () => {
        return (
            <Card>
                <Card.Header className="text-center">
                    <h5 className="my-2">Edytuj ogłoszenie </h5>
                </Card.Header>
                <Card.Body>
                    <BasicInformation
                        title={this.state.title}
                        onChangeTitle={this.onChangeTitle}
                        titleError={this.state.errors && this.state.errors.title}
                        location={this.state.location}
                        onChangeLocation={this.onChangeLocation}
                        locationError={this.state.errors && this.state.errors.location}
                        price={this.state.price}
                        onChangePrice={this.onChangePrice}
                        priceError={this.state.errors && this.state.errors.price}
                    />
                    <ContactInformation email={this.props.email}/>
                    <DescriptionForm
                        description={this.state.description}
                        onChangeDescription={this.onChangeDescription}
                        length={this.state.description.length}
                        descriptionError={this.state.errors && this.state.errors.description}
                    />
                    {/*<KeywordsInput*/}
                    {/*    ref={this.keywordsComponent}*/}
                    {/*    errors={this.state.errors && this.state.errors.keywords}*/}
                    {/*/>*/}
                    {/*<ImagesUpload*/}
                    {/*    ref={this.imagesComponent}*/}
                    {/*    errors={this.state.errors}*/}
                    {/*/>*/}
                    {/*<div className="text-center my-2">*/}
                    {/*    <ButtonWithSpinner*/}
                    {/*        content="Dodaj ogłoszenie"*/}
                    {/*        onClick={this.onClickSubmit}*/}
                    {/*        ongoingApiCall={this.state.ongoingApiCall}*/}
                    {/*    />*/}
                    {/*</div>*/}
                </Card.Body>
            </Card>
        );
    }

    returnSpinner = () => {
        return (
            <div className="text-center">
                <Spinner animation="border" size="sm" role="status" className="ms-1">
                    <span className="sr-only">Loading...</span>
                </Spinner>
            </div>
        );
    }

    render() {
        const content = this.state.isLoading ? this.returnSpinner() : this.returnMainContent();
        return (
            <Container data-testid="editnoticepage" className="my-3">
                {content}
            </Container>
        );
    }
}


EditNoticePage.defaultProps = {
    match: {
        params: {
            id: 0
        }
    }
}

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn,
        email: state.email,
        userId: state.id
    }
}

export default connect(mapStateToProps)(EditNoticePage);