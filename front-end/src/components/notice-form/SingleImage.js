import React from 'react';
import {Card, Image} from "react-bootstrap";
import defaultNoticeImage from "../../assets/default-notice-image.jpg";

const SimpleImage = ({image, onSelectImage, content, error}) => {
    const img = image ? image : defaultNoticeImage;

    return (
        <div className="col-12 col-sm-11 col-md-9 col-lg-5 col-xl-4 mx-auto mb-3 mx-1">
            <Card.Text as="div" className="mx-3 my-1">
                <div>
                    <Image src={img} className="croppedImage mx-auto d-inline-block"/>
                </div>
                <div className="my-2">
                    <div className="text-center">{content}</div>
                    <div className="custom-file mb-3 mx-auto mt-2">
                        <input className="form-control" type="file" id="formFile" onChange={onSelectImage}/>
                        {error && <div className="text-center">
                            <small className="text-danger">
                                {error}
                            </small>
                        </div>}
                    </div>
                </div>
            </Card.Text>
        </div>
    );
}

export default SimpleImage;
