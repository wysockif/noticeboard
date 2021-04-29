import React from 'react';
import {Card, FormControl, Image} from "react-bootstrap";
import defaultNoticeImage from "../../assets/default-notice-image.jpg";

const CreateNoticePageImages = () => {
    return (
            <Card className="mt-2">
                <div className="col-11 mx-auto row">
                    <Card.Title className="m-3 mb-1"><small>Zdjęcia</small></Card.Title>
                    <small className="mx-3 text-muted">Kolejność zdjęć ma znaczenie.</small>
                    <div className="col-12 col-sm-10 col-md-5 col-lg-5 col-xl-4 mx-auto mb-3">
                        <Card.Text as="div" className="mx-3 my-1">
                            <div>
                                <Image src={defaultNoticeImage} thumbnail/>
                            </div>
                            <div className="my-2">
                                <div className="text-center">Zdjęcie pierwszoplanowe</div>
                                <div className="custom-file mb-3 mx-auto mt-2">
                                    <input className="form-control" type="file" id="formFile"/>
                                    <FormControl.Feedback type="invalid" className="text-center">
                                        Coś tam
                                    </FormControl.Feedback>
                                </div>
                            </div>
                        </Card.Text>
                    </div>
                    <div className="col-12 col-sm-10 col-md-5 col-lg-5 col-xl-4 mx-auto">
                        <Card.Text as="div" className="mx-3 my-2">
                            <div>
                                <Image src={defaultNoticeImage} thumbnail/>
                            </div>
                            <div className="my-2">
                                <div className="text-center">Zdjęcie drugoplanowe</div>
                                <div className="custom-file mb-3 mx-auto mt-2">
                                    <input className="form-control" type="file" id="formFile"/>
                                    <FormControl.Feedback type="invalid" className="text-center">
                                        Coś tam
                                    </FormControl.Feedback>
                                </div>
                            </div>
                        </Card.Text>
                    </div>
                    <div className="col-12 col-sm-10 col-md-5 col-lg-5 col-xl-4 mx-auto">
                        <Card.Text as="div" className="mx-3 my-2">
                            <div>
                                <Image src={defaultNoticeImage} thumbnail/>
                            </div>
                            <div className="my-2">
                                <div className="text-center">Zdjęcie trzecioplanowe</div>
                                <div className="custom-file mb-3 mx-auto mt-2">
                                    <input className="form-control" type="file" id="formFile"/>
                                    <FormControl.Feedback type="invalid" className="text-center">
                                        Coś tam
                                    </FormControl.Feedback>
                                </div>
                            </div>
                        </Card.Text>
                    </div>
                </div>
            </Card>
    );
};

export default CreateNoticePageImages;
