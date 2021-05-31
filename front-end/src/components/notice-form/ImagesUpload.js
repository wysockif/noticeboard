import React, {Component} from 'react';
import {Card} from "react-bootstrap";
import SingleImage from "./SingleImage";

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

    onSelectPrimaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    if (this.props.errors) {
                        delete this.props.errors.primaryImage;
                    }
                    this.crop(fileReader.result, 4 / 3).then(croppedImage => {
                        this.setState({primaryImage: croppedImage.toDataURL(), primaryImageError: undefined});

                    });
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
                    this.crop(fileReader.result, 4 / 3).then(croppedImage => {
                        this.setState({secondaryImage: croppedImage.toDataURL(), secondaryImageError: undefined});
                    });
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
                    this.crop(fileReader.result, 4 / 3).then(croppedImage => {
                        this.setState({tertiaryImage: croppedImage.toDataURL(), tertiaryImageError: undefined});
                    });
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
                    <SingleImage
                        content="Zdjęcie pierwszoplanowe"
                        image={this.state.primaryImage}
                        onSelectImage={this.onSelectPrimaryImage}
                        error={this.state.primaryImageError || (this.props.errors && this.props.errors.primaryImage)}
                    />
                    <SingleImage
                        content="Zdjęcie drugoplanowe"
                        image={this.state.secondaryImage}
                        onSelectImage={this.onSelectSecondaryImage}
                        error={this.props.errors && this.props.errors.secondaryImage}
                    />
                    <SingleImage
                        content="Zdjęcie trzecioplanowe"
                        image={this.state.tertiaryImage}
                        onSelectImage={this.onSelectTertiaryImage}
                        error={this.props.errors && this.props.errors.tertiaryImage}
                    />
                    {this.state.test}
                </div>
            </Card>
        );
    };
}


export default ImagesUpload;
