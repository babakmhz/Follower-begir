package com.follow.nobahar.ViewModel;

public class UserIdsViewModel {

    private  Long userId;

    public UserIdsViewModel(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
