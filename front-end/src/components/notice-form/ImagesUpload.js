import React, {Component} from 'react';
import {Card} from "react-bootstrap";
import SimpleImage from "./SingleImage";

export class ImagesUpload extends Component {
    state = {
        primaryImage: '',
        secondaryImage: '',
        tertiaryImage: '',
        primaryImageError: undefined,
        secondaryImageError: undefined,
        tertiaryImageError: undefined
    }

    componentDidMount() {
        if (this.props.primaryImage) {
            this.setState({primaryImage: '/images/notice/' + this.props.primaryImage});
        }
        if (this.props.secondaryImage) {
            this.setState({secondaryImage: '/images/notice/' + this.props.secondaryImage});
        }
        if (this.props.tertiaryImage) {
            this.setState({tertiaryImage: '/images/notice/' + this.props.tertiaryImage});
        }
    }

    onSelectPrimaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    if (this.props.errors) {
                        delete this.props.errors.primaryImage;
                    }
                    this.setState({primaryImage: fileReader.result, primaryImageError: undefined});
                }
                fileReader.readAsDataURL(file);
            } else {
                this.setState({
                    primaryImageError: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                });
            }
        }
    }

    onSelectSecondaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    if (this.props.errors) {
                        delete this.props.errors.secondaryImage;
                    }
                    this.setState({secondaryImage: fileReader.result, secondaryImageError: undefined});
                }
                fileReader.readAsDataURL(file);
            } else {
                this.setState({
                    secondaryImageError: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                });
            }
        }
    }

    onSelectTertiaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    if (this.props.errors) {
                        delete this.props.errors.tertiaryImage;
                    }
                    this.setState({tertiaryImage: fileReader.result, tertiaryImageError: undefined});
                }
                fileReader.readAsDataURL(file);
            } else {
                this.setState({
                    tertiaryImageError: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                });
            }
        }
    }

    render() {
        return (
            <Card className="mt-2">
                <div className="col-11 mx-auto row">
                    <Card.Title className="m-3 mb-1"><small>Zdjęcia</small></Card.Title>
                    <small className="mx-3 text-muted">Kolejność zdjęć ma znaczenie.</small>
                    <SimpleImage
                        content="Zdjęcie pierwszoplanowe"
                        image={this.state.primaryImage}
                        onSelectImage={this.onSelectPrimaryImage}
                        error={this.state.primaryImageError || (this.props.errors && this.props.errors.primaryImage)}
                    />
                    <SimpleImage
                        content="Zdjęcie drugoplanowe"
                        image={this.state.secondaryImage}
                        onSelectImage={this.onSelectSecondaryImage}
                        error={this.props.errors && this.props.errors.secondaryImage}
                    />
                    <SimpleImage
                        content="Zdjęcie trzecioplanowe"
                        image={this.state.tertiaryImage}
                        onSelectImage={this.onSelectTertiaryImage}
                        error={this.props.errors && this.props.errors.tertiaryImage}
                    />
                </div>
            </Card>
        );
    };
}


export default ImagesUpload;
