import axios from 'axios';
import * as apiCalls from './apiCalls';

describe('apiCalls', () => {
    describe('register', () => {
        it('calls /api/1.0/users', () => {
            // given
            const mockRegister = jest.fn();
            axios.post = mockRegister;
            // when
            apiCalls.register();
            // then
            const calledPath = mockRegister.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users');
        });
    });

    describe('updateUser', () => {
        it('calls /api/1.0/users/123 when the 123 is provided for the function', () => {
            // given
            const id = '123';
            const mockUpdateUser = jest.fn();
            axios.patch = mockUpdateUser;
            // when
            apiCalls.updateUser(id);
            // then
            const calledPath = mockUpdateUser.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users/123');
        });
    });


    describe('Login', () => {
        it('calls /api/1.0/login', () => {
            // given
            const mockLogin = jest.fn();
            axios.post = mockLogin;
            // when
            apiCalls.login({username: 'username', password: 'Password123'});
            // then
            const calledPath = mockLogin.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/login');
        });
    });

    describe('getUser', () => {
        it('calls /api/1.0/users/username123 when the username123 is provided for the function', () => {
            // given
            const username = 'username123';
            const mockGetUser = jest.fn();
            axios.get = mockGetUser;
            // when
            apiCalls.getUser(username);
            // then
            const calledPath = mockGetUser.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users/username123');
        });
    });

    describe('getNoticesByUsername', () => {
        it('calls /api/1.0/notices?username=username123 when username123 is provided for the function', () => {
            // given
            const username = 'username123';
            const mockGetNoticesByUsername = jest.fn();
            axios.get = mockGetNoticesByUsername;
            // when
            apiCalls.getNoticesByUsername(username);
            // then
            const calledPath = mockGetNoticesByUsername.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices?username=username123&sort=createdAt,desc');
        });
    });

    describe('getNotices', () => {
        it('calls /api/1.0/notices?username=username123&sort=createdAt,desc when username123 is provided for the function', () => {
            // given
            const username = 'username123';
            const mockGetNoticesByUsername = jest.fn();
            axios.get = mockGetNoticesByUsername;
            // when
            apiCalls.getNoticesByUsername(username);
            // then
            const calledPath = mockGetNoticesByUsername.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices?username=username123&sort=createdAt,desc');
        });
    });

    describe('getNotices', () => {
        it('calls /api/1.0/notices?sort=createdAt,desc', () => {
            // given
            const username = 'username123';
            const mockGetNotices = jest.fn();
            axios.get = mockGetNotices;
            // when
            apiCalls.getNotices();
            // then
            const calledPath = mockGetNotices.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices?sort=createdAt,desc');
        });
    });


    describe('getNotice', () => {
        it('calls /api/1.0/notices/1 when 1 is provided for the function', () => {
            // given
            const noticeId = 1;
            const mockGetNotice = jest.fn();
            axios.get = mockGetNotice;
            // when
            apiCalls.getNotice(noticeId);
            // then
            const calledPath = mockGetNotice.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices/1');
        });
    });

    describe('postNotice', () => {
        it('calls /api/1.0/notices', () => {
            // given
            const mockPostNotice = jest.fn();
            axios.post = mockPostNotice;
            // when
            apiCalls.postNotice({});
            // then
            const calledPath = mockPostNotice.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices');
        });
    });

    describe('deleteNotice', () => {
        it('calls /api/1.0/notices/1 when 1 is provided for the function', () => {
            // given
            const mockDeleteNotice = jest.fn();
            axios.delete = mockDeleteNotice;
            // when
            apiCalls.deleteNotice(1);
            // then
            const calledPath = mockDeleteNotice.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices/1');
        });
    });
});