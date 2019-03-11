package com.example.ui.organizations.favorites

import com.example.data.models.Organization
import com.example.ui.organizations.OrganizationsContract

interface FavoriteOrganizationsContract {
    interface View : OrganizationsContract.View

    interface Presenter : OrganizationsContract.Presenter {
        fun onFavoriteChangeClick(organization: Organization)
    }
}
