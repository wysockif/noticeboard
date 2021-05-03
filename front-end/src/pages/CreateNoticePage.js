import React, {Component} from 'react';
import {Card, Container} from 'react-bootstrap';
import {connect} from 'react-redux';
import {Redirect} from 'react-router';
import ButtonWithSpinner from '../components/ButtonWithSpinner';
import BasicInformation from '../components/create-notice-page/BasicInformation';
import CreateNoticePageContact from '../components/create-notice-page/ContactInformation';
import DescriptionForm from '../components/create-notice-page/DescriptionForm';
import ImagesUpload from '../components/create-notice-page/ImagesUpload';
import KeywordsInput from '../components/create-notice-page/KeywordsInput';
import * as apiCalls from '../api/apiCalls';

class CreateNoticePage extends Component {

    state = {
        title: '',
        location: '',
        price: '',
        description: '',
        ongoingApiCall: false
    }

    keywordsComponent = React.createRef();
    imagesComponent = React.createRef();

    onClickSubmit = () => {
        const {title, location, price, description} = this.state;
        const keywords = this.keywordsComponent.current.state.keywords;
        const primaryImage = this.imagesComponent.current.state.primaryImage.split(',')[1];
        const secondaryImage = this.imagesComponent.current.state.secondaryImage.split(',')[1];
        const tertiaryImage = this.imagesComponent.current.state.tertiaryImage.split(',')[1];
        const notice = {
            title,
            location,
            price,
            description,
            keywords,
            primaryImage,
            secondaryImage,
            tertiaryImage
        }
        this.setState({pendingApiCall: true})
        apiCalls.postNotice(notice)
            .then(response => {
                this.props.history.push(`/notice/${response.data}`)
            })
            .catch(error => {

            });
    }

    onChangeTitle = event => {
        this.setState({title: event.target.value});
    }

    onChangeLocation = event => {
        this.setState({location: event.target.value});
    }

    onChangePrice = event => {
        this.setState({price: event.target.value});
    }

    onChangeDescription = event => {
        this.setState({description: event.target.value});
    }


    render() {
        return (
            <Container data-testid="createnoticepage" className="my-3">
                {!this.props.isLoggedIn && <Redirect to="/login"/>}
                <Card>
                    <Card.Header className="text-center">
                        <h5 className="my-2">Dodaj ogłoszenie </h5>
                    </Card.Header>
                    <Card.Body>
                        <BasicInformation
                            title={this.state.title}
                            onChangeTitle={this.onChangeTitle}
                            location={this.state.location}
                            onChangeLocation={this.onChangeLocation}
                            price={this.state.price}
                            onChangePrice={this.onChangePrice}
                        />
                        <CreateNoticePageContact email={this.props.email}/>
                        <DescriptionForm
                            description={this.state.description}
                            onChangeDescription={this.onChangeDescription}
                        />
                        <KeywordsInput ref={this.keywordsComponent}/>
                        <ImagesUpload ref={this.imagesComponent}/>
                        <div className="text-center my-2">
                            <ButtonWithSpinner
                                content="Dodaj ogłoszenie"
                                onClick={this.onClickSubmit}
                                ongoingApiCall={this.state.ongoingApiCall}
                            />
                        </div>
                    </Card.Body>
                </Card>
            </Container>
        );
    }
}

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn,
        email: state.email
    }
}

export default connect(mapStateToProps)(CreateNoticePage);