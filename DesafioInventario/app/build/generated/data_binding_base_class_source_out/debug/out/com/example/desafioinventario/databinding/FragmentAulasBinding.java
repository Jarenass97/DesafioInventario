// Generated by view binder compiler. Do not edit!
package com.example.desafioinventario.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.desafioinventario.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentAulasBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final FloatingActionButton btnAddAula;

  @NonNull
  public final RecyclerView rvAulas;

  private FragmentAulasBinding(@NonNull ConstraintLayout rootView,
      @NonNull FloatingActionButton btnAddAula, @NonNull RecyclerView rvAulas) {
    this.rootView = rootView;
    this.btnAddAula = btnAddAula;
    this.rvAulas = rvAulas;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentAulasBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentAulasBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_aulas, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentAulasBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnAddAula;
      FloatingActionButton btnAddAula = ViewBindings.findChildViewById(rootView, id);
      if (btnAddAula == null) {
        break missingId;
      }

      id = R.id.rvAulas;
      RecyclerView rvAulas = ViewBindings.findChildViewById(rootView, id);
      if (rvAulas == null) {
        break missingId;
      }

      return new FragmentAulasBinding((ConstraintLayout) rootView, btnAddAula, rvAulas);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
